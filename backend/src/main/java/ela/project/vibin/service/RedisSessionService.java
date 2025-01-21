package ela.project.vibin.service;

import ela.project.vibin.model.SessionData;
import ela.project.vibin.repository.redis.SessionDataRepository;
import org.springframework.stereotype.Service;

@Service
public class RedisSessionService {

    private final SessionDataRepository sessionDataRepository;

    public RedisSessionService(SessionDataRepository sessionDataRepository) {
        this.sessionDataRepository = sessionDataRepository;
    }

    public void storeSessionData(String state, String userId) {
        SessionData sessionData = new SessionData();
        sessionData.setState(state);
        sessionData.setUserId(userId);
        sessionDataRepository.save(sessionData);
    }

    public SessionData retrieveSessionData(String sessionId) {
        return sessionDataRepository.findById(sessionId).orElse(null);
    }

    public void clearSession(String sessionId) {
        sessionDataRepository.deleteById(sessionId);
    }
}

