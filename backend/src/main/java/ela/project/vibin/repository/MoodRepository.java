package ela.project.vibin.repository;

import ela.project.vibin.model.Mood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MoodRepository extends JpaRepository<Mood, UUID> {
    @Query("SELECT m FROM Mood m WHERE m.emotion.id = :emotion_id")
    List<Mood> findMoodsBy(@Param("emotion_id") UUID emotionId);
}
