package ela.project.vibin.controller;

import ela.project.vibin.model.Track;
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
            @RequestParam(value = "submission") String input) {
        // validate incoming client-side data
        if (!isValidInput(input)) {
            return new ResponseEntity<>("bro give me a valid string", HttpStatus.BAD_REQUEST);
        }
        // transform emotion to mood

        // transform mood to genre

        // generate playlist query

        // generate get playlist items query

        // generate track query

        // return track information

        return new ResponseEntity<String>("here are your tracks" ,HttpStatus.OK);
    }

    private boolean isValidInput(String input) {
        boolean isValidLength = input.length() > 3;
        return input != null && !input.isEmpty() && isValidLength;

    }
}
