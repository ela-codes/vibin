package ela.project.vibin.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("SessionData")
@Getter
@Setter
public class SessionData {
    @Id
    private String state;
    private String userId;
}
