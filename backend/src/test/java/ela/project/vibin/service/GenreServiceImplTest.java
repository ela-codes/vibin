package ela.project.vibin.service;

import ela.project.vibin.model.Emotion;
import ela.project.vibin.model.EmotionType;
import ela.project.vibin.model.Genre;
import ela.project.vibin.model.Mood;
import ela.project.vibin.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    @Test
    public void getAllGenreNames_validMoodIds_returnsListOfGenreNames() {
        // Arrange
        UUID moodId1 = UUID.randomUUID();
        UUID moodId2 = UUID.randomUUID();
        Genre genre1 = Genre.builder().id(UUID.randomUUID()).genre("acoustic").build();
        Genre genre2 = Genre.builder().id(UUID.randomUUID()).genre("blues").build();
        Genre genre3 = Genre.builder().id(UUID.randomUUID()).genre("jazz").build();

        when(genreRepository.findGenresBy(moodId1)).thenReturn(List.of(genre1, genre2));
        when(genreRepository.findGenresBy(moodId2)).thenReturn(List.of(genre3));

        List<String> expectedGenreNamesList = List.of("acoustic", "blues", "jazz");

        // Act
        List<String> actualGenreNamesList = genreService.getAllGenreNames(List.of(moodId1, moodId2));

        // Assert
        assertEquals(3, actualGenreNamesList.size());
        assertTrue(actualGenreNamesList.containsAll(expectedGenreNamesList));
    }

    @Test
    public void getAllGenreNames_emptyMoodIdList_returnsEmptyList() {
        // Arrange & Act
        List<String> actualGenreNamesList = genreService.getAllGenreNames(List.of());

        // Assert
        assertTrue(actualGenreNamesList.isEmpty());
    }

    @Test
    public void getAllGenreNames_noGenresForMoodIds_returnsEmptyList() {
        // Arrange
        UUID moodId1 = UUID.randomUUID();
        UUID moodId2 = UUID.randomUUID();

        when(genreRepository.findGenresBy(moodId1)).thenReturn(List.of());
        when(genreRepository.findGenresBy(moodId2)).thenReturn(List.of());

        // Act
        List<String> actualGenreNamesList = genreService.getAllGenreNames(List.of(moodId1, moodId2));

        // Assert
        assertTrue(actualGenreNamesList.isEmpty());
    }
}