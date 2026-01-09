package dev.melvstein.vocabulary_app.repository;

import dev.melvstein.vocabulary_app.model.AdminUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AdminUserRepository extends ReactiveMongoRepository<AdminUser, String> {
    Mono<AdminUser> findByUsername(String username);
    Mono<AdminUser> findByEmail(String email);
    Mono<AdminUser> findByUsernameOrEmail(String username, String email);
}
