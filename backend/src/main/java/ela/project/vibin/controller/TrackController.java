package ela.project.vibin.controller;

import ela.project.vibin.model.Track;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TrackController {

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

        // transform input to emotion

        // transform emotion to mood

        // transform mood to genre

        // generate playlist query

        // generate get playlist items query

        // generate track query

        // return track information

        return new ResponseEntity<String>("did I eat with these tracks?" ,HttpStatus.OK);
    }

    private boolean isValidInput(String input) {
        boolean isValidLength = input.length() > 3;
        return input != null && !input.isEmpty() && isValidLength;

    }
}
