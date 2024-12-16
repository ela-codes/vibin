package ela.project.vibin.controller;

import ela.project.vibin.model.EmotionType;
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
    public ResponseEntity<String> getTracks(
            @RequestParam(value = "submission") String input,
            HttpSession session) {

        // validate session principal
        // TODO - maybe check that the user is in the database?
        if (session.getAttribute("userId") == null) {
            return new ResponseEntity<>("um? you are NOT logged in", HttpStatus.UNAUTHORIZED);
        }

        // validate incoming client-side data
        if (!isValidInput(input)) {
            return new ResponseEntity<>("yo stop capping, pass a valid string", HttpStatus.BAD_REQUEST);
        }

        try {
            // transform input to emotion
            EmotionType emotionName = EmotionType.valueOf(emotionRecognitionServiceImpl.analyze(input));
            UUID emotionId = emotionServiceImpl.getEmotionId(emotionName);

            // get moods based on emotion
            List<UUID> moodIds = moodServiceImpl.getAllMoodIds(emotionId);

            // get genres based on moods
            List<String> genreNames = genreServiceImpl.getAllGenreNames(moodIds);

            // get playlist id based on genres
            String randomPlaylistId = spotifyQueryServiceImpl.getSpotifyPlaylistId(genreNames, session);

            // get tracks based on playlist id
            List<Track> tracksList = spotifyQueryServiceImpl.getSpotifyTracks(randomPlaylistId, session);

            return new ResponseEntity<>(tracksList.toString(), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Error analyzing emotion: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isValidInput(String input) {
        return input != null && input.length() > 2;

    }
}
