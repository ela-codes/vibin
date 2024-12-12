package ela.project.vibin.repository;

import ela.project.vibin.model.Emotion;
import ela.project.vibin.model.EmotionType;
import ela.project.vibin.model.Mood;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class MoodRepositoryTest {
    @Autowired
    private MoodRepository moodRepository;
    @Autowired
    private EmotionRepository emotionRepository;

    @Test
    public void findMoodsBy_validEmotionId_returnsListOfMoods() {
        // Arrange
        Emotion emotion = Emotion.builder()
                .emotion(EmotionType.valueOf("sadness"))
                .build();
        emotionRepository.save(emotion);

        Mood mood1 = Mood.builder()
                .mood("sad")
                .emotion(emotion)
                .build();

        Mood mood2 = Mood.builder()
                .mood("nostalgic")
                .emotion(emotion)
                .build();
        moodRepository.save(mood1);
        moodRepository.save(mood2);

        // Act
        List<Mood> moods = moodRepository.findMoodsBy(emotion.getId());

        // Assert
        assertEquals(2, moods.size());
        assertArrayEquals(new Mood[]{mood1, mood2}, moods.toArray());
    }

    @Test
    public void findMoodsBy_invalidEmotionId_returnsEmptyList() {
        // Arrange
        Emotion emotion = Emotion.builder()
                .emotion(EmotionType.valueOf("sadness"))
                .build();
        emotionRepository.save(emotion);

        Mood mood1 = Mood.builder()
                .mood("sad")
                .emotion(emotion)
                .build();

        Mood mood2 = Mood.builder()
                .mood("nostalgic")
                .emotion(emotion)
                .build();
        moodRepository.save(mood1);
        moodRepository.save(mood2);

        // Act
        List<Mood> moods = moodRepository.findMoodsBy(UUID.randomUUID());

        // Assert
        assertEquals(0, moods.size());
    }
}