package ela.project.vibin.service;

import ela.project.vibin.model.User;
import ela.project.vibin.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void saveUser_existingEmail_returnsExistingUser() {
        String email = "test123@xyz.com";
        User expectedUser = new User();
        expectedUser.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.saveUser(email);

        assertNotNull(actualUser);
        assertEquals(email, actualUser.getEmail());
    }

    @Test
    public void saveUser_nullEmail_throwsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.saveUser(null);
        });

        assertEquals("Email cannot be null or empty", exception.getMessage());
    }

    @Test
    public void saveUser_emptyEmail_throwsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.saveUser("");
        });

        assertEquals("Email cannot be null or empty", exception.getMessage());
    }
}