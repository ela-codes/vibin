package ela.project.vibin.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Validated
@Getter
@Setter
public class RedisPropertiesConfig {
    @NotEmpty(message = "Redis host cannot be empty")
    private String host;

    @NotNull(message = "Redis port cannot be empty")
    private int port;

    @NotEmpty(message = "Redis password cannot be empty")
    private String password;
}
