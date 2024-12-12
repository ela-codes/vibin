package ela.project.vibin.repository;

import ela.project.vibin.model.EmotionType;
import ela.project.vibin.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByEmail_validEmail_returnsUser() {
        // Arrange
        User user = User.builder()
                .email("test@xyz.com")
                .build();

        userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findByEmail("test@xyz.com");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
    }

    @Test
    public void findByEmail_invalidEmail_returnsEmptyOptional() {
        // Arrange
        User user = User.builder()
                .email("test@xyz.com")
                .build();

        userRepository.save(user);

        // Act
        Optional<User> result1 = userRepository.findByEmail("testing@xyz.com");
        Optional<User> result2 = userRepository.findByEmail("");

        // Assert
        assertFalse(result1.isPresent());
        assertFalse(result2.isPresent());
    }

}