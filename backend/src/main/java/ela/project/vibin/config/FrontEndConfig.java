package ela.project.vibin.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "frontend")
@Validated
@Getter
@Setter
public class FrontEndConfig {
    @NotEmpty(message = "Front End URL cannot be empty")
    private String url;
}
