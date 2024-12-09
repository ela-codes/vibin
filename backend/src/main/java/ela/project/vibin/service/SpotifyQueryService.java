package ela.project.vibin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ela.project.vibin.model.Track;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class SpotifyQueryService {


    public String getSpotifyPlaylistId(List<String> genreList, HttpSession session) {
        // Remove any duplicates in genreList
        Set<String> genreSet = new HashSet<>(genreList);

        // Set up playlist search query
        String genres = String.join("+", genreSet);

        Random rand = new Random();
        int randomOffset = rand.nextInt(21);

        String filters = String.format("&type=playlist&limit=1&offset=%d", randomOffset);
        String searchQuery = String.format("https://api.spotify.com/v1/search?q=%s%s", genres, filters);

        // Set up query headers
        HttpHeaders headers = new HttpHeaders();
        String userAccessToken = (String) session.getAttribute("accessToken"); // Retrieve from user session
        headers.set("Authorization", "Bearer " + userAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make the API call
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    URI.create(searchQuery), HttpMethod.GET, entity, String.class
            );

            // Check for error in response
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error with Spotify request: " + response.getBody());
            }

            // Get playlist ID from response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            return extractPlaylistId(rootNode);

        } catch (Exception e) {
            throw new RuntimeException("Error fetching playlist from Spotify: " + e.getMessage(), e);
        }
    }

    public List<String> getTrackIds() {
        return List.of("");
    }

    public List<Track> getTracks() {
        return List.of(new Track());
    }


    // helper function that extracts playlist ID from the JSON response body
    private String extractPlaylistId(JsonNode rootNode) {
        JsonNode playlistsNode = rootNode.path("playlists").path("items");
        if (playlistsNode.isArray() && !playlistsNode.isEmpty()) {
            return playlistsNode.get(0).path("id").asText();
        }
        return "";
    }
}
