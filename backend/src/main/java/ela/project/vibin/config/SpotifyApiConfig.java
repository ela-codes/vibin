package ela.project.vibin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.michaelthelin.spotify.SpotifyApi;

import java.net.URI;


@Configuration
public class SpotifyApiConfig {

    // value injects the value of the property into this variable
    @Value("${spotify.client.id")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    @Bean // this method will return a "SpotifyApi" object, managed by the Spring container
    public SpotifyApi spotifyApi() {
        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(URI.create(redirectUri)) // convert the string into URI object
                .build();
    }
}
