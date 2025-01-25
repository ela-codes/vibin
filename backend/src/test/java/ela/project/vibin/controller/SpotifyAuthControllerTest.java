package ela.project.vibin.controller;

import ela.project.vibin.config.FrontEndConfig;
import ela.project.vibin.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import jakarta.servlet.http.HttpSession;
import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpotifyAuthControllerTest {

    @Mock
    private SpotifyApi mockSpotifyApi;

    @Mock
    private UserServiceImpl mockUserServiceImpl;

    @Mock
    private FrontEndConfig frontEndConfig;

    private SpotifyAuthController controller;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new SpotifyAuthController(mockSpotifyApi, mockUserServiceImpl, frontEndConfig);
        request = new MockHttpServletRequest();
    }

    @Test
    void login_ShouldGenerateAuthorizationUrlAndStoreState() {
        // Arrange
        String expectedUrl = "https://accounts.spotify.com/authorize?client_id=test&response_type=code&redirect_uri=test";
        URI expectedUri = URI.create(expectedUrl);

        AuthorizationCodeUriRequest.Builder builder = mock(AuthorizationCodeUriRequest.Builder.class);
        AuthorizationCodeUriRequest authorizationCodeUriRequest = mock(AuthorizationCodeUriRequest.class);

        when(mockSpotifyApi.authorizationCodeUri()).thenReturn(builder);
        when(builder.state(anyString())).thenAnswer(invocation -> {
            // Simulate storing the state in session
            String state = invocation.getArgument(0, String.class);
            HttpSession session = request.getSession();
            session.setAttribute("state", state);
            return builder;
        });
        when(builder.scope("playlist-modify-public user-read-email")).thenReturn(builder);
        when(builder.show_dialog(true)).thenReturn(builder);
        when(builder.build()).thenReturn(authorizationCodeUriRequest);
        when(authorizationCodeUriRequest.execute()).thenReturn(expectedUri);

        // Act
        String result = controller.login(request.getSession());

        // Assert
        assertNotNull(result);
        assertEquals(expectedUrl, result);

        // Verify the state was stored in session
        HttpSession session = request.getSession();
        String storedState = (String) session.getAttribute("state");
        assertNotNull(storedState);
        assertEquals(UUID.fromString(storedState).toString(), storedState); // Ensure it's a valid UUID

        // Verify the builder was called with the correct configuration
        verify(builder).state(storedState);
        verify(builder).scope("playlist-modify-public user-read-email");
        verify(builder).show_dialog(true);
        verify(builder).build();
        verify(authorizationCodeUriRequest).execute();
    }

    @Test
    void login_ShouldHandleSpotifyApiException() {
        // Arrange
        AuthorizationCodeUriRequest.Builder builder = mock(AuthorizationCodeUriRequest.Builder.class);
        AuthorizationCodeUriRequest authorizationCodeUriRequest = mock(AuthorizationCodeUriRequest.class);

        when(mockSpotifyApi.authorizationCodeUri()).thenReturn(builder);
        when(builder.state(anyString())).thenReturn(builder);
        when(builder.scope("playlist-modify-public user-read-email")).thenReturn(builder);
        when(builder.show_dialog(true)).thenReturn(builder);
        when(builder.build()).thenReturn(authorizationCodeUriRequest);
        when(authorizationCodeUriRequest.execute()).thenThrow(new RuntimeException("Spotify API Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.login(request.getSession()));
    }
}
