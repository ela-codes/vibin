package ela.project.vibin.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "spotify")
@Validated
@Getter
@Setter
public class SpotifyPropertiesConfig {
    @NotEmpty(message = "Client ID cannot be empty")
    private String clientId;

    @NotEmpty(message = "Client Secret cannot be empty")
    private String clientSecret;

    @NotEmpty(message = "Redirect URI cannot be empty")
    private String redirectUri;
}
