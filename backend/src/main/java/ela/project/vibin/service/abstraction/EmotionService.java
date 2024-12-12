package ela.project.vibin.service.abstraction;

import ela.project.vibin.model.EmotionType;

import java.util.UUID;

public interface EmotionService {
    public UUID getEmotionId(EmotionType emotion);
}
