package dev.melvstein.vocabulary_app.service;

import dev.melvstein.vocabulary_app.Dto.ApiResponse;
import dev.melvstein.vocabulary_app.Dto.VocabularyDto;
import dev.melvstein.vocabulary_app.enums.ApiResponseCode;
import dev.melvstein.vocabulary_app.mapper.VocabularyMapper;
import dev.melvstein.vocabulary_app.model.User;
import dev.melvstein.vocabulary_app.model.Vocabulary;
import dev.melvstein.vocabulary_app.repository.UserRepository;
import dev.melvstein.vocabulary_app.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.node.ValueNode;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class VocabularyService extends BaseService {
    private final VocabularyRepository vocabularyRepository;
    private final UserRepository userRepository;
    private final VocabularyMapper vocabularyMapper;

    public Flux<Vocabulary> getVocabularies() {
        return vocabularyRepository.findAll();
    }

    public Flux<Vocabulary> getVocabulariesByUserId(String userId) {
        return vocabularyRepository.findAllByUserId(userId);
    }

    public Mono<Vocabulary> getVocabularyByUserIdAndWord(String userId, String word) {
        return vocabularyRepository.findByUserIdAndWord(userId, word);
    }

    public Mono<Vocabulary> addVocabulary(Vocabulary vocabulary) {
        return vocabularyRepository.save(vocabulary);
    }

    public Mono<Vocabulary> updateVocabulary(Vocabulary vocabulary) {
        return vocabularyRepository.findById(vocabulary.getId())
                .flatMap(existingVocabulary -> {;
                    if (vocabulary.getWord() != null) {
                        existingVocabulary.setWord(vocabulary.getWord());
                    }

                    if (vocabulary.getPartOfSpeech() != null) {
                        existingVocabulary.setPartOfSpeech(vocabulary.getPartOfSpeech());
                    }

                    if (vocabulary.getEnglishDefinition() != null) {
                        existingVocabulary.setEnglishDefinition(vocabulary.getEnglishDefinition());
                    }

                    if (vocabulary.getTagalogDefinition() != null) {
                        existingVocabulary.setTagalogDefinition(vocabulary.getTagalogDefinition());
                    }

                    if (vocabulary.getEnglishSynonyms() != null) {
                        existingVocabulary.setEnglishSynonyms(vocabulary.getEnglishSynonyms());
                    }

                    if (vocabulary.getTagalogSynonyms() != null) {
                        existingVocabulary.setTagalogSynonyms(vocabulary.getTagalogSynonyms());
                    }

                    if (vocabulary.getEnglishAntonyms() != null) {
                        existingVocabulary.setEnglishAntonyms(vocabulary.getEnglishAntonyms());
                    }

                    if (vocabulary.getTagalogAntonyms() != null) {
                        existingVocabulary.setTagalogAntonyms(vocabulary.getTagalogAntonyms());
                    }

                    if (vocabulary.getExampleSentence() != null) {
                        existingVocabulary.setExampleSentence(vocabulary.getExampleSentence());
                    }

                    return vocabularyRepository.save(existingVocabulary);
                });
    }

    public Mono<Vocabulary> deleteVocabularyById(String id) {
        return vocabularyRepository.findById(id)
                .flatMap(vocabulary ->
                        vocabularyRepository.delete(vocabulary)
                                .then(Mono.just(vocabulary))
                );
    }
}
