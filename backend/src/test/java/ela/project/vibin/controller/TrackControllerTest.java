package ela.project.vibin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import ela.project.vibin.model.EmotionType;
import ela.project.vibin.model.ErrorResponse;
import ela.project.vibin.model.Track;
import ela.project.vibin.service.EmotionRecognitionServiceImpl;
import ela.project.vibin.service.EmotionServiceImpl;
import ela.project.vibin.service.GenreServiceImpl;
import ela.project.vibin.service.MoodServiceImpl;
import ela.project.vibin.service.SpotifyQueryServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrackControllerTest {

    @Mock
    private EmotionRecognitionServiceImpl emotionRecognitionServiceImpl;

    @Mock
    private EmotionServiceImpl emotionServiceImpl;

    @Mock
    private MoodServiceImpl moodServiceImpl;

    @Mock
    private GenreServiceImpl genreServiceImpl;

    @Mock
    private SpotifyQueryServiceImpl spotifyQueryServiceImpl;

    @InjectMocks
    private TrackController trackController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTracks_missingUserId_returnsHttpStatusUnauthorized() {
        // Arrange
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("userId")).thenReturn(null);

        // Act
        ResponseEntity<?> response = trackController.getTracks("valid input", session);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getTracks_invalidInput_returnsHttpStatusBadRequest() {
        // Arrange
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("userId")).thenReturn("someUserId");

        // Act
        ResponseEntity<?> response1 = trackController.getTracks("xy", session);
        ResponseEntity<?> response2 = trackController.getTracks(null, session);
        ResponseEntity<?> response3 = trackController.getTracks(" ", session);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    void getTracks_emotionRecognitionException_returnsHttpStatusInternalServerError() throws JsonProcessingException {
        // Arrange
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("userId")).thenReturn("someUserId");
        when(emotionRecognitionServiceImpl.isModelReady()).thenReturn(true);
        when(emotionRecognitionServiceImpl.analyze(anyString(), anyBoolean())).thenThrow(new RuntimeException("Test Exception"));

        // Act
        ResponseEntity<?> response = trackController.getTracks("valid input", session);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getTracks_validInput_returnsListOfTracks() throws JsonProcessingException {
        // Arrange
        Track track1 = new Track("track 1", "trackId1");
        Track track2 = new Track("track 2", "trackId2");

        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("userId")).thenReturn("someUserId");
        when(emotionRecognitionServiceImpl.isModelReady()).thenReturn(true);
        when(emotionRecognitionServiceImpl.analyze("valid input", false)).thenReturn("joy");
        when(emotionServiceImpl.getEmotionId(EmotionType.joy)).thenReturn(UUID.randomUUID());
        when(moodServiceImpl.getAllMoodIds(any(UUID.class))).thenReturn(List.of(UUID.randomUUID()));
        when(genreServiceImpl.getAllGenreNames(anyList())).thenReturn(List.of("pop"));
        when(spotifyQueryServiceImpl.getSpotifyPlaylistId(anyList(), any(HttpSession.class))).thenReturn("playlistId");
        when(spotifyQueryServiceImpl.getSpotifyTracks(anyString(), any(HttpSession.class)))
                .thenReturn(List.of(track1, track2));

        // Act
        ResponseEntity<?> response = trackController.getTracks("valid input", session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof List<?>);
        List<Track> trackResponse = (List<Track>) response.getBody();
        assertTrue(trackResponse.contains(track1));
        assertTrue(trackResponse.contains(track2));
    }
}