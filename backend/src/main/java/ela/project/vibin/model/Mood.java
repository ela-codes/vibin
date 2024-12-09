package ela.project.vibin.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "mood")
@Getter
@Setter
public class Mood {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String mood;

    @ManyToOne
    @JoinColumn(name = "emotion_id", nullable = false)
    private Emotion emotion; // many moods can belong to one emotion

//
//    @ManyToMany(mappedBy = "moods")
//    private Set<Genre> genres = new HashSet<>();

}
