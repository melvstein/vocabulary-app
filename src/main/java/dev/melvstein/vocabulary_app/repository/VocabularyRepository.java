package dev.melvstein.vocabulary_app.repository;

import dev.melvstein.vocabulary_app.model.Vocabulary;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface VocabularyRepository extends ReactiveMongoRepository<Vocabulary, String> {
    Flux<Vocabulary> findAllByUserId(String userId);
    Mono<Vocabulary> findByUserIdAndWord(String userId, String word);
}
