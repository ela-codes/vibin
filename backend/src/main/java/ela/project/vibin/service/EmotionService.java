package ela.project.vibin.service;

import ela.project.vibin.model.Emotion;
import ela.project.vibin.model.EmotionType;
import ela.project.vibin.repository.EmotionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmotionService {
    private final EmotionRepository emotionRepository;

    public EmotionService(EmotionRepository emotionRepository) {
        this.emotionRepository = emotionRepository;
    }

    public UUID getEmotionId(EmotionType emotion) {
        Emotion retrievedEmotion = emotionRepository.findByEmotion(emotion);
        return retrievedEmotion.getId();
    }
}
