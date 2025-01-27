package ela.project.vibin.controller;

import ela.project.vibin.config.FrontEndConfig;
import ela.project.vibin.service.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class SpotifyAuthController {

    private final SpotifyApi spotifyApi;
    private final UserServiceImpl userServiceImpl;
    private final FrontEndConfig frontEndConfig;
    private final StringRedisTemplate redisTemplate;

    public SpotifyAuthController(SpotifyApi spotifyApi, UserServiceImpl userServiceImpl,
                                 FrontEndConfig frontEndConfig, StringRedisTemplate redisTemplate) {
        this.spotifyApi = spotifyApi;
        this.userServiceImpl = userServiceImpl;
        this.frontEndConfig = frontEndConfig;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        String generatedState = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set("spotify:state:" + generatedState, generatedState, Duration.ofMinutes(5));

        AuthorizationCodeUriRequest authRequest = spotifyApi.authorizationCodeUri()
                .state(generatedState)
                .scope("playlist-modify-public user-read-email")
                .show_dialog(true)
                .build();

        final URI uri = authRequest.execute();

        return uri.toString();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleSpotifyCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state") String spotifyState,
            @RequestParam(value = "error", defaultValue = "") String error) {

        System.out.println("Code: " + code);
        System.out.println("Spotify state: " + spotifyState);
        System.out.println("Error: " + error);

        if (!error.isEmpty()) {
            return ResponseEntity
                    .status(302)
                    .header("Location", frontEndConfig.getUrl() + "/?error=UserDeniedAuthorization")
                    .build();
        }

        // Retrieve the stored state from Redis
        String storedState = redisTemplate.opsForValue().get("spotify:state:" + spotifyState);

        System.out.println("Stored State: " + storedState);
        System.out.println("Received State: " + spotifyState);

        // Compare the state from the response from Spotify with the stored state in Redis
        if (storedState == null) {
            return ResponseEntity.status(302)
                    .header("Location", frontEndConfig.getUrl() + "/?error=InvalidState-StateNull")
                    .build();
        }

        if (!spotifyState.equals(storedState)) {
            return ResponseEntity.status(302)
                    .header("Location", frontEndConfig.getUrl() + "/?error=InvalidState-NotAMatch")
                    .build();
        }

        try {
            AuthorizationCodeRequest authorizationCodeRequest = spotifyApi
                    .authorizationCode(code)
                    .build();

            AuthorizationCodeCredentials credentials = authorizationCodeRequest.execute();

            spotifyApi.setAccessToken(credentials.getAccessToken());
            spotifyApi.setRefreshToken(credentials.getRefreshToken());

            // Build the user object
            User user = spotifyApi.getCurrentUsersProfile()
                    .build()
                    .execute();

            userServiceImpl.saveUser(user.getEmail());

            // Save user info to session
            redisTemplate.opsForValue().set("spotify:user-name:" + user.getId(), user.getDisplayName(), Duration.ofHours(1));
            redisTemplate.opsForValue().set("spotify:user-access:" + user.getId(), credentials.getAccessToken(), Duration.ofHours(1));

            return ResponseEntity.status(302)
                    .header("Location",
                            frontEndConfig.getUrl() + "/home?userId=" + URLEncoder.encode(
                                    user.getId(), StandardCharsets.UTF_8
                            ))
                    .build();

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during Spotify callback: " + e.getMessage());
        }
    }

    @GetMapping("/user-details")
    public ResponseEntity<Map<String, String>> getUserDetails(@RequestParam("userId") String userId) {
        // Retrieve user details from Redis
        String displayName = redisTemplate.opsForValue().get("spotify:user-name:" + userId);

        if (displayName == null) {
            return ResponseEntity.status(401).body(null); // Unauthorized
        }

        return ResponseEntity.ok(Map.of("displayName", displayName));
    }
}



