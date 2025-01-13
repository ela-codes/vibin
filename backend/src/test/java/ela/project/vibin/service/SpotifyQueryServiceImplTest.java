package ela.project.vibin.service;

import ela.project.vibin.model.Track;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;

import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SpotifyQueryServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpSession session;

    @InjectMocks
    private SpotifyQueryServiceImpl spotifyQueryService;

    @Test
    public void getSpotifyPlaylistId_validGenreList_returnsPlaylistId() {
        // Arrange
        String expectedPlaylistId = "abc123";

        // Mock session
        String mockAccessToken = "mockAccessToken";
        Mockito.when(session.getAttribute("accessToken")).thenReturn(mockAccessToken);

        // Mock RestTemplate response
        String mockResponse = """
                {
                    "playlists": {
                        "items": [
                            {
                                "id": "abc123"
                            }
                        ]
                    }
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + mockAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> mockRequest = new HttpEntity<>(headers);

        Mockito.when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.GET),
                Mockito.eq(mockRequest),
                Mockito.eq(String.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // Act
        List<String> actualGenreList = List.of("pop", "rock");
        String actualPlaylistId = spotifyQueryService.getSpotifyPlaylistId(actualGenreList, session);

        // Assert the result
        assertEquals(expectedPlaylistId, actualPlaylistId);
    }

    @Test
    public void getSpotifyPlaylistId_emptyGenreList_throwsRuntimeException() {
        // Arrange
        String mockAccessToken = "mockAccessToken";

        // Mock session
        Mockito.when(session.getAttribute("accessToken")).thenReturn(mockAccessToken);

        // Mock Spotify API error response for an empty query
        String mockResponse = """
            {
              "error": {
                "status": 400,
                "message": "No search query"
              }
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + mockAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> mockRequest = new HttpEntity<>(headers);

        // Mock the RestTemplate exchange to return a 400 Bad Request
        Mockito.when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.GET),
                Mockito.eq(mockRequest),
                Mockito.eq(String.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.BAD_REQUEST));

        // Execute the method
        Exception actualException = assertThrows(RuntimeException.class, () -> {
            spotifyQueryService.getSpotifyPlaylistId(List.of(), session);
        });

        // Verify the exception message contains the error message
        assertTrue(actualException.getMessage().contains("No search query"));
    }

    @Test
    public void getSpotifyTracks_validPlaylistId_returnsTracks() {
        // Arrange
        String mockPlaylistId = "validPlaylistId";

        // Mock session
        String mockAccessToken = "mockAccessToken";
        Mockito.when(session.getAttribute("accessToken")).thenReturn(mockAccessToken);

        // Mock Spotify API response
        String mockResponse = """
            {
              "total": 2,
              "items": [
                {
                  "track": {
                    "name": "Track 1",
                    "id": "track1"
                  }
                },
                {
                  "track": {
                    "name": "Track 2",
                    "id": "track2"
                  }
                }
              ]
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + mockAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> mockRequest = new HttpEntity<>(headers);

        // Mock the RestTemplate exchange to return the mock response
        Mockito.when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.GET),
                Mockito.eq(mockRequest),
                Mockito.eq(String.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // Execute the method
        List<Track> tracks = spotifyQueryService.getSpotifyTracks(mockPlaylistId, session);

        // Assert the returned list
        assertNotNull(tracks);
        assertEquals(2, tracks.size());

        // Assert track details
        assertTrue(tracks.get(0).equals(new Track("Track 1", "track1")));
        assertTrue(tracks.get(1).equals(new Track("Track 2", "track2")));
    }

    @Test
    public void getSpotifyTracks_invalidPlaylistId_throwsRuntimeException() {
        // Arrange
        String mockPlaylistId = "invalidPlaylistId";

        // Mock session
        String mockAccessToken = "mockAccessToken";
        Mockito.when(session.getAttribute("accessToken")).thenReturn(mockAccessToken);

        // Mock Spotify API response
        String mockResponse = """
            {
              "error": {
                "status": 400,
                "message": "Invalid base62 id"
              }
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + mockAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> mockRequest = new HttpEntity<>(headers);

        // Mock the RestTemplate exchange to return the mock response
        Mockito.when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.GET),
                Mockito.eq(mockRequest),
                Mockito.eq(String.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.BAD_REQUEST));

        // Execute the method
        Exception actualException = assertThrows(RuntimeException.class, () -> {
            spotifyQueryService.getSpotifyTracks(mockPlaylistId, session);
        });

        // Assert
        assertTrue(actualException.getMessage().contains("Invalid base62 id"));
    }

}