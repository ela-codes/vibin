package ela.project.vibin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.michaelthelin.spotify.SpotifyApi;

import java.net.URI;


@Configuration
public class SpotifyApiConfig {

    private final SpotifyPropertiesConfig spotifyPropertiesConfig;


    public SpotifyApiConfig(SpotifyPropertiesConfig spotifyPropertiesConfig) {
        this.spotifyPropertiesConfig = spotifyPropertiesConfig;
    }


    @Bean
    public SpotifyApi spotifyApi() {
        return new SpotifyApi.Builder()
                .setClientId(spotifyPropertiesConfig.getClientId())
                .setClientSecret(spotifyPropertiesConfig.getClientSecret())
                .setRedirectUri(URI.create(spotifyPropertiesConfig.getRedirectUri()))
                .build();
    }
}