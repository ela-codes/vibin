package ela.project.vibin.service;

import ela.project.vibin.model.User;
import ela.project.vibin.repository.UserRepository;
import ela.project.vibin.service.abstraction.UserService;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User saveUser(String email) {
        if(email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return userRepository.findByEmail(email).orElseGet(() -> createUser(email));
    }


    private User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        return userRepository.save(user);
    }

}
