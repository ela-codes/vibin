package ela.project.vibin;

import ela.project.vibin.controller.SpotifyAuthController;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SpotifyAuthControllerTest {
    private SpotifyAuthController spotifyAuthController;

    @Mock
    private SpotifyApi mockSpotifyApi;

    @Mock
    private AuthorizationCodeUriRequest mockAuthRequest;

    @Mock
    private HttpSession mockSession;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.spotifyAuthController = new SpotifyAuthController(mockSpotifyApi);

        // Mock the SpotifyApi behavior
        when(mockSpotifyApi.authorizationCodeUri()).thenReturn(mockAuthRequest);
        when(mockAuthRequest.scope(anyString())).thenReturn(mockAuthRequest);
        when(mockAuthRequest.state(anyString())).thenReturn(mockAuthRequest);
        when(mockAuthRequest.build()).thenReturn(mockAuthRequest);
        when(mockAuthRequest.execute()).thenReturn(URI.create("https://accounts.spotify.com/authorize?state=mockState"));


    }


    @Test
    public void spotifyApiLogin_ShouldReturnAuthorizationUrlWithState() throws Exception {
        // Act
        ResponseEntity<String> response = spotifyAuthController.login(mockSession);

        // Assert that a state was generated and stored in the session
        ArgumentCaptor<String> stateCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSession).setAttribute(eq("oauth_state"), stateCaptor.capture());
        String capturedState = stateCaptor.getValue();

        assertNotNull(capturedState);
        assertFalse(capturedState.isEmpty(), "State should not be empty");

        // Verify that the AuthorizationCodeUriRequest was built with the correct state
        verify(mockAuthRequest).state(eq(capturedState));
        verify(mockAuthRequest).scope(eq("playlist-modify-public user-read-email"));

        // Verify the response contains the Spotify authorization URL
        assertEquals("https://accounts.spotify.com/authorize?state=mockState", response.getBody());
    }
}
