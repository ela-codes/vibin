package ela.project.vibin.service.abstraction;

import ela.project.vibin.model.Track;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface SpotifyQueryService {
    public String getSpotifyPlaylistId(List<String> genreList, HttpSession session);
    public List<Track> getSpotifyTracks(String playlistId, HttpSession session);
}
