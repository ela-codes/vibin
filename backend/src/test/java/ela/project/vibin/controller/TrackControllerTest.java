package ela.project.vibin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import ela.project.vibin.model.EmotionType;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
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

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    void getTracks_emotionRecognitionException_returnsHttpStatusInternalServerError() throws JsonProcessingException {
        // Arrange
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("userId")).thenReturn("someUserId");
        when(emotionRecognitionServiceImpl.analyze(anyString())).thenThrow(new RuntimeException("Test Exception"));

        // Act
        ResponseEntity<?> response = trackController.getTracks("valid input", session);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getTracks_validInput_returnsListOfTracks() throws JsonProcessingException {
        // Arrange
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("userId")).thenReturn("someUserId");
        when(emotionRecognitionServiceImpl.analyze("valid input")).thenReturn("joy");
        when(emotionServiceImpl.getEmotionId(EmotionType.joy)).thenReturn(UUID.randomUUID());
        when(moodServiceImpl.getAllMoodIds(any(UUID.class))).thenReturn(List.of(UUID.randomUUID()));
        when(genreServiceImpl.getAllGenreNames(anyList())).thenReturn(List.of("pop"));
        when(spotifyQueryServiceImpl.getSpotifyPlaylistId(anyList(), any(HttpSession.class))).thenReturn("playlistId");
        when(spotifyQueryServiceImpl.getSpotifyTracks(anyString(), any(HttpSession.class)))
                .thenReturn(List.of(
                        new Track("track 1", "trackId1"),
                        new Track("track 2", "trackId2")));

        // Act
        ResponseEntity<?> response = trackController.getTracks("valid input", session);
        List<Track> trackResponse = (List<Track>) response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(trackResponse);
        assertTrue(trackResponse.contains("track 1"));
        assertTrue(trackResponse.contains("track 2"));
    }
}