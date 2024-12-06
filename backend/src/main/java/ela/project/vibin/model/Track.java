package ela.project.vibin.model;

import lombok.Data;

@Data
public class Track {
    private String trackName;

    private String artistName;

    private String trackUrl;

    private String artistUrl;

    private String trackLength;

    private boolean explicit;

    private String albumImageUrl;

    private String albumUrl;
}
