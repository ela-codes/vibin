package ela.project.vibin.repository;

import ela.project.vibin.model.Emotion;
import ela.project.vibin.model.EmotionType;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2) // uses in-memory db for testing
class EmotionRepositoryTest {

    @Autowired
    private EmotionRepository emotionRepository;

    @Test
    public void findByEmotion_validEmotionType_returnsEmotion() {
        // Arrange
        Emotion emotion = Emotion.builder()
                .emotion(EmotionType.valueOf("love"))
                .build();
        emotionRepository.save(emotion);

        // Act
        Optional<Emotion> foundEmotion = emotionRepository.findByEmotion(EmotionType.valueOf("love"));

        // Assert
        assertTrue(foundEmotion.isPresent());
        assertEquals(emotion, foundEmotion.get());
    }

    @Test
    public void findByEmotion_invalidEmotionType_throwsException() {
        // Arrange
        for (EmotionType emotionType : EmotionType.values()) {
            Emotion emotion = Emotion.builder()
                    .emotion(emotionType)
                    .build();
            emotionRepository.save(emotion);
        }

        // Act & Assert
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            emotionRepository.findByEmotion(EmotionType.valueOf("invalid"))
                    .orElseThrow(() -> new IllegalArgumentException("Emotion not found"));
        });
    }

}