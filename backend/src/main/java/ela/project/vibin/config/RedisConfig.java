package ela.project.vibin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = "ela.project.vibin.repository.redis")
public class RedisConfig {
}
