package ela.project.vibin.service;

import ela.project.vibin.model.Emotion;
import ela.project.vibin.model.EmotionType;
import ela.project.vibin.repository.EmotionRepository;
import ela.project.vibin.service.abstraction.EmotionService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmotionServiceImpl implements EmotionService {
    private final EmotionRepository emotionRepository;

    public EmotionServiceImpl(EmotionRepository emotionRepository) {
        this.emotionRepository = emotionRepository;
    }

    @Override
    public UUID getEmotionId(EmotionType emotion) {
        Emotion retrievedEmotion = emotionRepository.findByEmotion(emotion)
                .orElseThrow(() -> new IllegalArgumentException("Emotion not found"));

        return retrievedEmotion.getId();
    }
}
