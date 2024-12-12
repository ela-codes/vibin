package ela.project.vibin.repository;

import ela.project.vibin.model.Emotion;
import ela.project.vibin.model.EmotionType;
import ela.project.vibin.model.Genre;
import ela.project.vibin.model.Mood;
import org.junit.jupiter.api.BeforeEach;
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
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MoodRepository moodRepository;

    @Autowired
    private EmotionRepository emotionRepository;

    @Test
    public void findGenresBy_validMoodId_returnsListOfGenres() {
        // Arrange
        Emotion emotion = Emotion.builder()
                .emotion(EmotionType.valueOf("sadness"))
                .build();
        emotionRepository.save(emotion);

        Mood mood1 = Mood.builder()
                .mood("sad")
                .emotion(emotion)
                .build();

        moodRepository.save(mood1);

        Genre genre1 = Genre.builder()
                .genre("nostalgic")
                .mood(mood1)
                .build();

        Genre genre2 = Genre.builder()
                .genre("acoustic")
                .mood(mood1)
                .build();

        genreRepository.save(genre1);
        genreRepository.save(genre2);

        // Act
        List<Genre> genres = genreRepository.findGenresBy(mood1.getId());

        // Assert
        assertEquals(2, genres.size());
        assertArrayEquals(new Genre[]{genre1, genre2}, genres.toArray());
    }


    @Test
    public void findGenresBy_invalidMoodId_returnsEmptyList() {
        // Arrange
        Emotion emotion = Emotion.builder()
                .emotion(EmotionType.valueOf("sadness"))
                .build();
        emotionRepository.save(emotion);

        Mood mood1 = Mood.builder()
                .mood("sad")
                .emotion(emotion)
                .build();

        moodRepository.save(mood1);

        Genre genre1 = Genre.builder()
                .genre("nostalgic")
                .mood(mood1)
                .build();

        genreRepository.save(genre1);


        // Act
        List<Genre> genres = genreRepository.findGenresBy(UUID.randomUUID());

        // Assert
        assertEquals(0, genres.size());
    }
}