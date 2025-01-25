package ela.project.vibin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import ela.project.vibin.model.EmotionType;
import ela.project.vibin.model.ErrorResponse;
import ela.project.vibin.model.Track;
import ela.project.vibin.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class TrackController {

    private final EmotionRecognitionServiceImpl emotionRecognitionServiceImpl;
    private final EmotionServiceImpl emotionServiceImpl;
    private final MoodServiceImpl moodServiceImpl;
    private final GenreServiceImpl genreServiceImpl;
    private final SpotifyQueryServiceImpl spotifyQueryServiceImpl;

    public TrackController(EmotionRecognitionServiceImpl emotionRecognitionServiceImpl,
                           EmotionServiceImpl emotionServiceImpl,
                           MoodServiceImpl moodServiceImpl,
                           GenreServiceImpl genreServiceImpl,
                           SpotifyQueryServiceImpl spotifyQueryServiceImpl) {
        this.emotionRecognitionServiceImpl = emotionRecognitionServiceImpl;
        this.emotionServiceImpl = emotionServiceImpl;
        this.moodServiceImpl = moodServiceImpl;
        this.genreServiceImpl = genreServiceImpl;
        this.spotifyQueryServiceImpl = spotifyQueryServiceImpl;
    }

    @GetMapping("/get-tracks")
    public ResponseEntity<?> getTracks(
            @RequestParam(value = "submission") String input,
            HttpSession session) {

        boolean waitForModel = false;

        // validate session principal
        if (session.getAttribute("userId") == null) {
            return new ResponseEntity<>(new ErrorResponse("Unauthorized access."), HttpStatus.UNAUTHORIZED);
        }

        // validate incoming client-side data
        if (!isValidInput(input)) {
            return new ResponseEntity<>(new ErrorResponse("Invalid request."), HttpStatus.BAD_REQUEST);
        }

        // check if model is ready
        if (!emotionRecognitionServiceImpl.isModelReady()) {
            // if not ready, indicate that we need to set wait-for-model flag = true
            waitForModel = true;
        }

        try {
            // transform input to emotion
            EmotionType emotionName = EmotionType.valueOf(emotionRecognitionServiceImpl.analyze(input, waitForModel));
            UUID emotionId = emotionServiceImpl.getEmotionId(emotionName);

            // get moods based on emotion
            List<UUID> moodIds = moodServiceImpl.getAllMoodIds(emotionId);

            // get genres based on moods
            List<String> genreNames = genreServiceImpl.getAllGenreNames(moodIds);

            // get playlist id based on genres
            String randomPlaylistId = spotifyQueryServiceImpl.getSpotifyPlaylistId(genreNames, session);

            // get tracks based on playlist id
            List<Track> tracksList = spotifyQueryServiceImpl.getSpotifyTracks(randomPlaylistId, session);

            return new ResponseEntity<>(tracksList, HttpStatus.OK);

        }  catch (JsonProcessingException e) {
            return new ResponseEntity<>(new ErrorResponse("JsonProcessingException error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ErrorResponse("RuntimeException error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private boolean isValidInput(String input) {
        return input != null && input.length() > 2;

    }
}
