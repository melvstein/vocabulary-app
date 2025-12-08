package dev.melvstein.vocabulary_app.repository;

import dev.melvstein.vocabulary_app.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
}
