package ela.project.vibin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ela.project.vibin.model.Track;
import ela.project.vibin.service.abstraction.SpotifyQueryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

import static java.lang.Math.min;

@Service
public class SpotifyQueryServiceImpl implements SpotifyQueryService {

    private final RestTemplate restTemplate;

    public SpotifyQueryServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getSpotifyPlaylistId(List<String> genreList, HttpSession session) {

        // Set up playlist search query
        String genres = mapGenreListToString(genreList);

        // Generate random int between 0-20 both inclusive
        int randomOffset = getRandomInt(21);

        // Build search query
        final int PLAYLIST_LIMIT = 20;

        String filters = String.format("&type=playlist&limit=%d&offset=%d", PLAYLIST_LIMIT, randomOffset);
        String searchQuery = String.format("https://api.spotify.com/v1/search?q=%s%s", genres, filters);

        // Set up query
        HttpEntity<String> entity = createEntityForSpotifyRequest((String) session.getAttribute("accessToken"));

        // Make the API call
        try {
            ResponseEntity<String> response = sendSpotifyGetRequest(searchQuery, entity);

            // Check for error in response
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error with getSpotifyPlaylistId request: " + response.getBody());
            }

            // Get playlist ID from response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            String retrievedPlaylistId = extractPlaylistId(rootNode);

            if (retrievedPlaylistId.isEmpty()) {
                throw new RuntimeException("No playlist found for the given genres.");
            } else {
                return retrievedPlaylistId;
            }

        } catch (Exception e) {
            throw new RuntimeException("Error fetching playlist from Spotify: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Track> getSpotifyTracks(String playlistId, HttpSession session) {
        // Generate random int between 0-10 both inclusive
        int randomOffset = getRandomInt(11);

        // Build search query
        final int TRACK_LIMIT = 50;

        String fields = "fields=limit%2Ctotal%2Citems%28track%28name%2Cid%29%29";
        String filters = String.format("&limit=%d&offset=%d", TRACK_LIMIT, randomOffset);

        String searchQuery = String.format(
                "https://api.spotify.com/v1/playlists/%s/tracks?%s%s",
                playlistId, fields, filters);

        HttpEntity<String> entity = createEntityForSpotifyRequest((String) session.getAttribute("accessToken"));

        try {
            ResponseEntity<String> response = sendSpotifyGetRequest(searchQuery, entity);

            // Check for error in response
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error with getSpotifyTracks request: " + response.getBody());
            }

            // Get tracks from response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            // check how many tracks in the playlist
            int totalTracks = min(TRACK_LIMIT, rootNode.path("total").asInt());

            return getRandomTracks(rootNode.path("items"), totalTracks);

        } catch (Exception e) {
            throw new RuntimeException("Error fetching tracks from Spotify: " + e.getMessage(), e);
        }
    }


    // helper function that extracts playlist ID from the JSON response body
    private String extractPlaylistId(JsonNode rootNode) {
        JsonNode playlistsNode = rootNode.path("playlists").path("items");

        if (playlistsNode.isArray() && !playlistsNode.isEmpty()) {
            for (int i = 0; i < playlistsNode.size(); i++) {
                JsonNode playlistItem = playlistsNode.get(i);

                if (playlistItem != null && playlistItem.isObject()) {
                    return playlistItem.path("id").asText();
                }
            }
        }
        return "";
    }

    // helper function that builds List<Track> from the JSON response body
    private List<Track> getRandomTracks(JsonNode playlistItems, int totalTracks) {
        // return up to 5 random tracks
        int max = min(totalTracks, 5);

        List<Integer> alreadyPicked = new ArrayList<>();

        List<Track> trackList = new ArrayList<>();

        if (totalTracks < 6) {
            for (int i = 0; i < totalTracks; i++) {
                trackList.add(new Track(
                        playlistItems.get(i).path("track").path("name").asText(),
                        playlistItems.get(i).path("track").path("id").asText())
                );
            }
        } else {
            for (int i = 0; i < max; i++) {
                int randomIdx = getRandomInt(totalTracks);

                // make sure we don't pick the same track twice
                while (alreadyPicked.contains(randomIdx)){
                    randomIdx = getRandomInt(totalTracks);
                }

                alreadyPicked.add(randomIdx);

                trackList.add(new Track(
                        playlistItems.get(randomIdx).path("track").path("name").asText(),
                        playlistItems.get(randomIdx).path("track").path("id").asText())
                );
            }
        }
        return trackList;
    }

    // helper function that creates a HttpEntity with the required spotify request headers
    private HttpEntity<String> createEntityForSpotifyRequest(String userAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + userAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(headers);
    }

    private ResponseEntity<String> sendSpotifyGetRequest(String searchQuery, HttpEntity<String> entity) {
        ResponseEntity<String> response = restTemplate.exchange(
                URI.create(searchQuery), HttpMethod.GET, entity, String.class
        );
        return response;
    }

    private int getRandomInt(int max) {
        Random rand = new Random();
        return rand.nextInt(max);
    }

    private String mapGenreListToString(List<String> genres) {
        // Remove any duplicates in genreList
        Set<String> genreSet = new HashSet<>();

        // Randomize order of genres to avoid repetitive genre results
        List<String> genreList = new ArrayList<>(genres);
        Collections.shuffle(genreList);

        // Ensure spaces inside genre names are replaced with '+'
        for (String genre : genreList) {
            String properGenreFormat = genre.replace(" ", "+").replace("&", "%26").replace("-", "+");
            genreSet.add(properGenreFormat);
        }
        // Map genreSet to a single string
        String formattedGenres = String.join("+", genreSet);

        return formattedGenres;
    }
}
