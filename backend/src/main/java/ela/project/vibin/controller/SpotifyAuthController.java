package ela.project.vibin.controller;

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
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class SpotifyAuthController {

    private final SpotifyApi spotifyApi;

    // constructor injection
    public SpotifyAuthController(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
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
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpSession session) {

        // validate the state parameter to ensure it's the same one we passed when making the request
        String storedState = (String) session.getAttribute("oauth_state");

        if (storedState == null || !storedState.equals(state)) {
            return ResponseEntity.badRequest().body("Invalid state parameter");
        }

        try {
            // exchange code for an access token
            AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();

            AuthorizationCodeCredentials credentials = authorizationCodeRequest.execute();

            // Set the access and refresh tokens in the SpotifyApi instance
            spotifyApi.setAccessToken(credentials.getAccessToken());
            spotifyApi.setRefreshToken(credentials.getRefreshToken());

            // get the user's profile using the access token
            User user = spotifyApi.getCurrentUsersProfile()
                    .build()
                    .execute();

            // TODO: Save the user's email to the database :D

            return ResponseEntity.ok("Welcome, " + user.getDisplayName() + "! Your account is connected.");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during Spotify callback: " + e.getMessage());
        }

    }

}
