
package ela.project.vibin.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "genre")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String genre;

    @ManyToOne
    @JoinColumn(name = "mood_id", nullable = false)
    private Mood mood; // many moods can belong to one emotion


//    @ManyToMany
//    @JoinTable(
//            name = "genre_mood", // name of the many-to-many join table
//            joinColumns = @JoinColumn(name = "genre_id"), // FK to Genre
//            inverseJoinColumns = @JoinColumn(name = "mood_id") // FK to Mood
//    )
//    private List<Mood> moods = new ArrayList<>();
}
