package ela.project.vibin.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "huggingface")
@Validated
@Getter
@Setter
public class HuggingFaceApiPropertiesConfig {
    @NotEmpty(message = "HuggingFace API URL cannot be empty")
    private String apiUrl;

    @NotEmpty(message = "HuggingFace API cannot be empty")
    private String apiToken;

}