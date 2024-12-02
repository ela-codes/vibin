package ela.project.vibin.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

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
    public ResponseEntity<String> login(HttpSession session) {
        String state = UUID.randomUUID().toString();

        // store the random UUID value in the HTTP session
        session.setAttribute("oauth_state", state);

        AuthorizationCodeUriRequest authRequest = spotifyApi.authorizationCodeUri()
            .state(state)
            .scope("playlist-modify-public user-read-email")
            .build();
        return ResponseEntity.ok(authRequest.execute().toString());
    }
}
