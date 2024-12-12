package ela.project.vibin.service.abstraction;

import java.util.List;
import java.util.UUID;

public interface MoodService {
    public List<UUID> getAllMoodIds(UUID emotionId);
}
