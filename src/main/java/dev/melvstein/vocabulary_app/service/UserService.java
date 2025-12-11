package dev.melvstein.vocabulary_app.service;

import dev.melvstein.vocabulary_app.model.User;
import dev.melvstein.vocabulary_app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService extends BaseService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        super(passwordEncoder);
        this.userRepository = userRepository;
    }

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Mono<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Mono<User> saveUser(User user) {
        user.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Mono<User> deleteUserById(String id) {
        return userRepository.findById(id)
                .flatMap(user ->
                        userRepository.delete(user)
                                .then(Mono.just(user))
                );
    }
}
