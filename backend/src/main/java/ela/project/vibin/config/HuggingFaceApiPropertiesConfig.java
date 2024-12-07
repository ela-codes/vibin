package ela.project.vibin.config;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "HuggingFace API URL cannot be null")
    @NotEmpty(message = "HuggingFace API URL cannot be empty")
    private String apiUrl;

    @NotNull(message = "HuggingFace API token cannot be null")
    @NotEmpty(message = "HuggingFace API cannot be empty")
    private String apiToken;

}