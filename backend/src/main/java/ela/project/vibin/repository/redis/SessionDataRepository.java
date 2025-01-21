package ela.project.vibin.repository.redis;

import ela.project.vibin.model.SessionData;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

public interface SessionDataRepository extends KeyValueRepository<SessionData, String> {
}
