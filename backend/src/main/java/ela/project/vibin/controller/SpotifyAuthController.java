package ela.project.vibin.controller;

import ela.project.vibin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class SpotifyAuthController {

    private final SpotifyApi spotifyApi;
    private final UserService userService;

    // constructor injection
    public SpotifyAuthController(SpotifyApi spotifyApi, UserService userService) {
        this.spotifyApi = spotifyApi;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        String state = UUID.randomUUID().toString();

        // store the random UUID value in the HTTP session
        session.setAttribute("oauth_state", state);

        AuthorizationCodeUriRequest authRequest = spotifyApi.authorizationCodeUri()
            .state(state)
            .scope("playlist-modify-public user-read-email")
            .show_dialog(true)
            .build();

        final URI uri = authRequest.execute();

        return uri.toString();
    }

    // after user authorizes spotify request, exchange code & state for tokens
    @GetMapping("/callback")
    public ResponseEntity<String> handleSpotifyCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state") String state,
            @RequestParam(value = "error", defaultValue = "") String error,
            HttpSession session) {

        // If user did not accept the spotify authorization request, stop auth flow
        if (!error.isEmpty()) {
            return ResponseEntity.badRequest().body("User did not accept the authorization request");
        }

        // validate the state parameter to ensure it's the same one we passed when making the request
        String storedState = (String) session.getAttribute("oauth_state");

        // If there is a mismatch then reject the request and stop auth flow
        if (storedState == null || !storedState.equals(state)) {
            return ResponseEntity.badRequest().body("Invalid state parameter");
        }

        try {
            // if state matches, then exchange code for an access token

            AuthorizationCodeRequest authorizationCodeRequest = spotifyApi
                    .authorizationCode(code) // includes spotify's required request body parameters
                    .build(); // includes spotify's required request header parameters

            AuthorizationCodeCredentials credentials = authorizationCodeRequest.execute();

            // Set the access and refresh tokens in the SpotifyApi instance using requested tokens
            spotifyApi.setAccessToken(credentials.getAccessToken()); // expires in 3600 seconds
            spotifyApi.setRefreshToken(credentials.getRefreshToken());

            System.out.println(credentials);

            // get the user's profile using the access token
            User user = spotifyApi.getCurrentUsersProfile()
                    .build()
                    .execute();

            // Save the user's email to the database
            userService.saveUser(user.getEmail());

            // return 200 OK with the welcome message in the response body
            return ResponseEntity.ok("Welcome, " + user.getDisplayName() + "! Your account is connected.");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during Spotify callback: " + e.getMessage());
        }

    }

}
