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

    private final EmotionRecognitionService emotionRecognitionService;
    private final EmotionService emotionService;
    private final MoodService moodService;
    private final GenreService genreService;
    private final SpotifyQueryService spotifyQueryService;

    public TrackController(EmotionRecognitionService emotionRecognitionService,
                           EmotionService emotionService,
                           MoodService moodService,
                           GenreService genreService,
                           SpotifyQueryService spotifyQueryService) {
        this.emotionRecognitionService = emotionRecognitionService;
        this.emotionService = emotionService;
        this.moodService = moodService;
        this.genreService = genreService;
        this.spotifyQueryService = spotifyQueryService;
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
            EmotionType emotionName = EmotionType.valueOf(emotionRecognitionService.analyze(input));
            UUID emotionId = emotionService.getEmotionId(emotionName);

            // get moods based on emotion
            List<UUID> moodIds = moodService.getAllMoodIds(emotionId);

            // get genres based on moods
            List<String> genreNames = genreService.getAllGenreNames(moodIds);

            // get playlist id based on genres
            String randomPlaylistId = spotifyQueryService.getSpotifyPlaylistId(genreNames, session);

            // get tracks based on playlist id
            List<Track> tracksList = spotifyQueryService.getSpotifyTracks(randomPlaylistId, session);

//            return new ResponseEntity<>(
//                    String.format("You're mostly feeling: %s%n%n%s%n%s%n%s", emotionName,
//                            "Here's my recommendation: ", tracksList,
//                            randomPlaylistId),
//                    HttpStatus.OK);
            return new ResponseEntity<>(tracksList.toString(), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Error analyzing emotion: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isValidInput(String input) {
        return input != null && input.length() > 3;

    }
}
