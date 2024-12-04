package ela.project.vibin.service;

import ela.project.vibin.model.User;
import ela.project.vibin.repository.UserRepository;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(String email) {
        return userRepository.findByEmail(email).orElseGet(() -> createUser(email));
    }

    private User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        return userRepository.save(user);
    }

}
