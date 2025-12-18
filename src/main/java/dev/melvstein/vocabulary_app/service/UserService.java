package dev.melvstein.vocabulary_app.service;

import dev.melvstein.vocabulary_app.model.User;
import dev.melvstein.vocabulary_app.model.Vocabulary;
import dev.melvstein.vocabulary_app.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService extends BaseService {
    private final UserRepository userRepository;
    @Getter
    private final PasswordEncoder passwordEncoder;

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
