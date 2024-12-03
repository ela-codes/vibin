package ela.project.vibin;

import ela.project.vibin.controller.SpotifyAuthController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpSession;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SpotifyAuthControllerTest {

    @Mock
    private SpotifyApi mockSpotifyApi;


    private MockHttpSession mockSession;

    @Mock
    private AuthorizationCodeCredentials mockCredentials;

    @Mock
    private User mockUser;

    private SpotifyAuthController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new SpotifyAuthController(mockSpotifyApi);
        mockSession = new MockHttpSession();
    }

    @Test
    void login_ShouldGenerateAuthorizationUrl() {
        // Arrange
        String expectedUrl = "https://accounts.spotify.com/authorize?client_id=test&response_type=code&redirect_uri=test";
        URI expectedUri = URI.create(expectedUrl);

        // Create mocks for both the Builder and the Request
        AuthorizationCodeUriRequest.Builder builder = mock(AuthorizationCodeUriRequest.Builder.class);
        AuthorizationCodeUriRequest request = mock(AuthorizationCodeUriRequest.class);

        // Set up the chain of method calls
        when(mockSpotifyApi.authorizationCodeUri()).thenReturn(builder);
        when(builder.state(anyString())).thenReturn(builder);
        when(builder.scope(anyString())).thenReturn(builder);
        when(builder.show_dialog(anyBoolean())).thenReturn(builder);
        when(builder.build()).thenReturn(request);
        when(request.execute()).thenReturn(expectedUri);

        // Act
        String result = controller.login(mockSession);

        // Assert
        assertNotNull(result);
        assertEquals(expectedUrl, result);
        assertNotNull(mockSession.getAttribute("oauth_state"));

        // Verify the builder methods were called
        verify(builder).state(any());
        verify(builder).scope("playlist-modify-public user-read-email");
        verify(builder).show_dialog(true);
        verify(builder).build();
        verify(request).execute();
    }

    @Test
    void login_ShouldStoreStateInSession() {
        // Arrange
        AuthorizationCodeUriRequest.Builder builder = mock(AuthorizationCodeUriRequest.Builder.class);
        AuthorizationCodeUriRequest request = mock(AuthorizationCodeUriRequest.class);

        when(mockSpotifyApi.authorizationCodeUri()).thenReturn(builder);
        when(builder.state(anyString())).thenReturn(builder);
        when(builder.scope(anyString())).thenReturn(builder);
        when(builder.show_dialog(anyBoolean())).thenReturn(builder);
        when(builder.build()).thenReturn(request);
        when(request.execute()).thenReturn(URI.create("https://test.com"));

        // Act
        controller.login(mockSession);

        // Assert
        String storedState = (String) mockSession.getAttribute("oauth_state");
        assertNotNull(storedState);
        assertTrue(storedState.length() > 0);
    }

    @Test
    void login_ShouldHandleSpotifyApiException() {
        // Arrange
        AuthorizationCodeUriRequest.Builder builder = mock(AuthorizationCodeUriRequest.Builder.class);
        AuthorizationCodeUriRequest request = mock(AuthorizationCodeUriRequest.class);

        when(mockSpotifyApi.authorizationCodeUri()).thenReturn(builder);
        when(builder.state(anyString())).thenReturn(builder);
        when(builder.scope(anyString())).thenReturn(builder);
        when(builder.show_dialog(anyBoolean())).thenReturn(builder);
        when(builder.build()).thenReturn(request);
        when(request.execute()).thenThrow(new RuntimeException("Spotify API Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.login(mockSession));
    }


}