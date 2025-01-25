package ela.project.vibin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@Configuration(proxyBeanMethods = false)
@EnableRedisHttpSession
public class RedisHttpSessionConfig {

    private final RedisPropertiesConfig redisPropertiesConfig;

    public RedisHttpSessionConfig(RedisPropertiesConfig redisPropertiesConfig) {
        this.redisPropertiesConfig = redisPropertiesConfig;
    }

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisPropertiesConfig.getHost());
        redisConfig.setPort(redisPropertiesConfig.getPort());
        redisConfig.setPassword(redisPropertiesConfig.getPassword());
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .useSsl()
                .build();

        return new LettuceConnectionFactory(redisConfig, clientConfig);
    }
}