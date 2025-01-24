package ela.project.vibin.controller;

import ela.project.vibin.config.FrontEndConfig;
import ela.project.vibin.service.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
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


    public SpotifyAuthController(SpotifyApi spotifyApi, UserServiceImpl userServiceImpl,
                                 FrontEndConfig frontEndConfig) {
        this.spotifyApi = spotifyApi;
        this.userServiceImpl = userServiceImpl;
        this.frontEndConfig = frontEndConfig;
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        String generatedState = UUID.randomUUID().toString();

        // Store the random UUID value in Redis
        session.setAttribute("state", generatedState);

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
            @RequestParam(value = "error", defaultValue = "") String error,
            HttpSession session) {

        if (!error.isEmpty()) {
            return ResponseEntity
                    .status(302)
                    .header("Location", frontEndConfig.getUrl() + "/?error=UserDeniedAuthorization")
                    .build();
        }

        // Retrieve the stored state from Redis
        String storedState = (String) session.getAttribute("state");

        // Compare the state from the response from Spotify with the stored state in Redis
        if (storedState == null || !spotifyState.equals(storedState)) {
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

            // Build the user object
            User user = spotifyApi.getCurrentUsersProfile()
                    .build()
                    .execute();

            userServiceImpl.saveUser(user.getEmail());

            // Save user info to session
            session.setAttribute("userId", user.getId());
            session.setAttribute("displayName", user.getDisplayName());
            session.setAttribute("accessToken", credentials.getAccessToken());

            return ResponseEntity.status(302)
                    .header("Location", frontEndConfig.getUrl() + "/home")
                    .build();

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during Spotify callback: " + e.getMessage());
        }
    }

    @GetMapping("/user-details")
    public ResponseEntity<Map<String, String>> getUserDetails(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        String displayName = (String) session.getAttribute("displayName");

        if (userId == null) {
            return ResponseEntity.status(401).body(null); // Unauthorized
        }

        return ResponseEntity.ok(Map.of("userId", userId, "displayName", displayName));
    }
}



