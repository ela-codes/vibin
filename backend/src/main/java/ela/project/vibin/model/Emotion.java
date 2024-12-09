package ela.project.vibin.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "emotion")
@Getter
@Setter
public class Emotion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private EmotionType emotion;

//    @OneToMany(mappedBy = "emotion")
//    private Set<Mood> moods = new HashSet<>();
}
