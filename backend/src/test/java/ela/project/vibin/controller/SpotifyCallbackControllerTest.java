package ela.project.vibin.controller;

import ela.project.vibin.config.FrontEndConfig;
import ela.project.vibin.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class SpotifyCallbackControllerTest {

    @Mock
    private SpotifyApi mockSpotifyApi;

    @Mock
    private UserServiceImpl mockUserServiceImpl;

    @Mock
    private MockHttpSession mockSession;

    @Mock
    private AuthorizationCodeCredentials mockCredentials;

    @Mock
    private FrontEndConfig frontEndConfig;

    @Mock
    private User mockUser;

    private SpotifyAuthController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new SpotifyAuthController(mockSpotifyApi, mockUserServiceImpl, frontEndConfig);
    }

    @Test
    void callback_ShouldReturnWelcomeMessage_WhenStateIsValid() throws Exception {
        // Arrange
        AuthorizationCodeRequest.Builder mockAuthCodeBuilder = mock(AuthorizationCodeRequest.Builder.class);
        AuthorizationCodeRequest mockAuthCodeRequest = mock(AuthorizationCodeRequest.class);

        GetCurrentUsersProfileRequest.Builder mockUserProfileBuilder = mock(GetCurrentUsersProfileRequest.Builder.class);
        GetCurrentUsersProfileRequest mockUserProfileRequest = mock(GetCurrentUsersProfileRequest.class);

        String validState = "valid_state";
        String code = "auth_code";
        String accessToken = "mock_access_token";
        String refreshToken = "mock_refresh_token";
        String error = "";

        // Mock session state
        when(mockSession.getAttribute("state")).thenReturn(validState);

        // Mock authorization code flow
        when(mockSpotifyApi.authorizationCode(code)).thenReturn(mockAuthCodeBuilder);
        when(mockAuthCodeBuilder.build()).thenReturn(mockAuthCodeRequest);
        when(mockAuthCodeRequest.execute()).thenReturn(mockCredentials);
        when(mockCredentials.getAccessToken()).thenReturn(accessToken);
        when(mockCredentials.getRefreshToken()).thenReturn(refreshToken);

        // Mock user profile retrieval
        when(mockSpotifyApi.getCurrentUsersProfile()).thenReturn(mockUserProfileBuilder);
        when(mockUserProfileBuilder.build()).thenReturn(mockUserProfileRequest);
        when(mockUserProfileRequest.execute()).thenReturn(mockUser);
        when(mockUser.getDisplayName()).thenReturn("John Doe");

        // Act
        ResponseEntity<String> response = controller.handleSpotifyCallback(code, validState, error, mockSession);

        // Assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        // Verify interactions
        verify(mockSession).getAttribute("state");
        verify(mockAuthCodeBuilder).build();
        verify(mockAuthCodeRequest).execute();

        verify(mockSpotifyApi).authorizationCode(code);
        verify(mockSpotifyApi).setAccessToken(accessToken);
        verify(mockSpotifyApi).setRefreshToken(refreshToken);
        verify(mockSpotifyApi).getCurrentUsersProfile();
        verify(mockUserProfileBuilder).build();
        verify(mockUserProfileRequest).execute();
    }

    @Test
    void callback_ShouldReturnBadRequest_WhenStateIsInvalid() {
        // Arrange
        String code = "auth_code";
        String invalidState = "invalid_state";
        String error = "";
        when(mockSession.getAttribute("oauth_state")).thenReturn("valid_state");

        // Act
        ResponseEntity<String> response = controller.handleSpotifyCallback(code, invalidState, error, mockSession);

        // Assert
        assertEquals(HttpStatusCode.valueOf(302), response.getStatusCode());
    }

    @Test
    void callback_ShouldReturnBadRequest_WhenAuthAccessDenied() {
        // Arrange
        String validState = "valid_state";
        String error = "access_denied";

        // Act
        ResponseEntity<String> response = controller.handleSpotifyCallback(null, validState, error, mockSession);

        // Assert
        assertEquals(HttpStatusCode.valueOf(302), response.getStatusCode());
    }

}
