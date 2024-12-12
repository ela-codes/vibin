package ela.project.vibin.service;

import ela.project.vibin.model.Emotion;
import ela.project.vibin.model.EmotionType;
import ela.project.vibin.repository.EmotionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmotionServiceImplTest {

    @Mock
    private EmotionRepository emotionRepository;

    @InjectMocks
    private EmotionServiceImpl emotionService;

    @Test
    public void getEmotionId_existingEmotion_returnsEmotionId() {
        // Arrange
        Emotion emotion = Emotion.builder()
                .emotion(EmotionType.valueOf("sadness"))
                .build();

        when(emotionRepository.findByEmotion(EmotionType.sadness)).thenReturn(Optional.ofNullable(emotion));

        // Act
        UUID retrievedId = emotionService.getEmotionId(EmotionType.sadness);

        // Assert
        assertEquals(emotion.getId(), retrievedId);
    }

}