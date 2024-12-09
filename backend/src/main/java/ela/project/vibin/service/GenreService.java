package ela.project.vibin.service;

import ela.project.vibin.model.Genre;
import ela.project.vibin.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<String> getAllGenreNames(List<UUID> moodIdList) {
        List<String> genreNameList = new ArrayList<String>();

        // find genres mapped to each mood
        for (UUID moodId : moodIdList) {
            List<Genre> genres = genreRepository.findGenresBy(moodId);

            // get the genre name and add it to list
            for (Genre genre : genres) {
                genreNameList.add(genre.getGenre());
            }
        }

        return genreNameList;
    }
}
