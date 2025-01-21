package ela.project.vibin.controller;

import ela.project.vibin.config.FrontEndConfig;
import ela.project.vibin.model.SessionData;
import ela.project.vibin.service.RedisSessionService;
import ela.project.vibin.service.UserServiceImpl;
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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class SpotifyAuthController {

    private final SpotifyApi spotifyApi;
    private final UserServiceImpl userServiceImpl;
    private final FrontEndConfig frontEndConfig;
    private final RedisSessionService redisSessionService;

    public SpotifyAuthController(SpotifyApi spotifyApi, UserServiceImpl userServiceImpl,
                                 FrontEndConfig frontEndConfig, RedisSessionService redisSessionService) {
        this.spotifyApi = spotifyApi;
        this.userServiceImpl = userServiceImpl;
        this.frontEndConfig = frontEndConfig;
        this.redisSessionService = redisSessionService;
    }

    @GetMapping("/login")
    public String login() {
        String state = UUID.randomUUID().toString();

        // Store the random UUID value in Redis
        redisSessionService.storeSessionData(state, null);

        AuthorizationCodeUriRequest authRequest = spotifyApi.authorizationCodeUri()
                .state(state)
                .scope("playlist-modify-public user-read-email")
                .show_dialog(true)
                .build();

        final URI uri = authRequest.execute();

        return uri.toString();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleSpotifyCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state") String state,
            @RequestParam(value = "error", defaultValue = "") String error) {

        if (!error.isEmpty()) {
            return ResponseEntity
                    .status(302)
                    .header("Location", frontEndConfig.getUrl() + "/?error=UserDeniedAuthorization")
                    .build();
        }

        // Retrieve the stored state from Redis
        SessionData sessionData = redisSessionService.retrieveSessionData(state);

        if (sessionData == null || !state.equals(sessionData.getState())) {
            return ResponseEntity.status(302)
                    .header("Location", frontEndConfig.getUrl() + "/?error=InvalidState")
                    .build();
        }

        try {
            AuthorizationCodeRequest authorizationCodeRequest = spotifyApi
                    .authorizationCode(code)
                    .build();

            AuthorizationCodeCredentials credentials = authorizationCodeRequest.execute();

            spotifyApi.setAccessToken(credentials.getAccessToken());
            spotifyApi.setRefreshToken(credentials.getRefreshToken());

            User user = spotifyApi.getCurrentUsersProfile()
                    .build()
                    .execute();

            userServiceImpl.saveUser(user.getEmail());

            // Update user details in Redis
            sessionData.setUserId(user.getId());
            redisSessionService.storeSessionData(sessionData.getState(), user.getId());
            System.out.println(sessionData.getState() + ", " + sessionData.getUserId());

            return ResponseEntity.status(302)
                    .header("Location", frontEndConfig.getUrl() + "/home")
                    .build();

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during Spotify callback: " + e.getMessage());
        }
    }

    @GetMapping("/user-details")
    public ResponseEntity<Map<String, String>> getUserDetails(@RequestParam("state") String state) {
        SessionData sessionData = redisSessionService.retrieveSessionData(state);

        if (sessionData == null || sessionData.getUserId() == null) {
            return ResponseEntity.status(401).body(null);
        }

        Map<String, String> userDetails = Map.of(
                "userId", sessionData.getUserId()
        );
        return ResponseEntity.ok(userDetails);
    }

}

