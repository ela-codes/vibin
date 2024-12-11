package ela.project.vibin.repository;

import ela.project.vibin.model.Emotion;
import ela.project.vibin.model.EmotionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmotionRepository extends JpaRepository<Emotion, UUID> {
    @Query("SELECT e FROM Emotion e WHERE e.emotion = :emotion")
    Optional<Emotion> findByEmotion(@Param("emotion") EmotionType emotion);

}
