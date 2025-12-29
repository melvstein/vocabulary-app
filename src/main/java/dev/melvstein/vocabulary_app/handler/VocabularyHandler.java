package dev.melvstein.vocabulary_app.handler;

import dev.melvstein.vocabulary_app.Dto.ApiResponse;
import dev.melvstein.vocabulary_app.Dto.VocabularyDto;
import dev.melvstein.vocabulary_app.enums.ApiResponseCode;
import dev.melvstein.vocabulary_app.mapper.VocabularyMapper;
import dev.melvstein.vocabulary_app.model.Vocabulary;
import dev.melvstein.vocabulary_app.service.UserService;
import dev.melvstein.vocabulary_app.service.VocabularyService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class VocabularyHandler {
    private final VocabularyService vocabularyService;
    private final VocabularyMapper vocabularyMapper;
    private final UserService userService;
    private final Validator validator;

    public Mono<ServerResponse> getVocabularies(ServerRequest request) {
        return vocabularyService.getVocabularies()
                .map(vocabularyMapper::toDto)
                .collectList()
                .flatMap(vocabularies -> {
                    return ServerResponse.ok().bodyValue(
                            ApiResponse.<List<VocabularyDto>>builder()
                                    .code(ApiResponseCode.SUCCESS.getCode())
                                    .message(ApiResponseCode.SUCCESS.getMessage())
                                    .data(vocabularies)
                                    .build()
                    );
                });
    }

    public Mono<ServerResponse> getVocabulariesByUserId(ServerRequest request) {
        String userId = request.pathVariable("userId");

        return userService.getUserById(userId)
                .flatMap(user -> {
                            return vocabularyService.getVocabulariesByUserId(user.getId())
                                    .map(vocabularyMapper::toDto)
                                    .collectList()
                                    .flatMap(vocabularies -> {
                                        return ServerResponse.ok().bodyValue(
                                                ApiResponse.<List<VocabularyDto>>builder()
                                                        .code(ApiResponseCode.SUCCESS.getCode())
                                                        .message(ApiResponseCode.SUCCESS.getMessage())
                                                        .data(vocabularies)
                                                        .build()
                                        );
                                    });
                })
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(
                        ApiResponse.builder()
                                .code(ApiResponseCode.ERROR.getCode())
                                .message("Empty vocabulary list for user id: " + userId)
                                .data(null)
                                .build()
                ));
    }

    public Mono<ServerResponse> addVocabulary(ServerRequest request) {
        return request.bodyToMono(VocabularyDto.class)
                .flatMap(vocabularyService::validateRequest)
                .flatMap(vocabularyRequest -> {
                    return userService.getUserById(vocabularyRequest.userId())
                            .flatMap(user -> {
                                return vocabularyService.getVocabularyByUserIdAndWord(user.getId(), vocabularyRequest.word())
                                        .flatMap(existingVocabulary -> {
                                            log.info("Method::addVocabulary -> Vocabulary already exists for userId: {} and word: {}",
                                                    user.getId(), vocabularyRequest.word());

                                            return ServerResponse.status(HttpStatus.CONFLICT).bodyValue(
                                                    ApiResponse.builder()
                                                            .code(ApiResponseCode.ERROR.getCode())
                                                            .message("Vocabulary already exists for the given userId and word")
                                                            .data(null)
                                                            .build()
                                            );
                                        })
                                        .switchIfEmpty(
                                                vocabularyService.addVocabulary(vocabularyMapper.toDocument(vocabularyRequest))
                                                        .map(vocabularyMapper::toDto)
                                                        .flatMap(savedVocabularyDto -> {
                                                            log.info("Method::addVocabulary -> Vocabulary added successfully for userId: {} and word: {}",
                                                                    vocabularyRequest.userId(), vocabularyRequest.word());

                                                            return ServerResponse.status(HttpStatus.CREATED).bodyValue(
                                                                    ApiResponse.<VocabularyDto>builder()
                                                                            .code(ApiResponseCode.SUCCESS.getCode())
                                                                            .message("Vocabulary added successfully")
                                                                            .data(savedVocabularyDto)
                                                                            .build()
                                                            );
                                                        })
                                        );
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                log.info("Method::addVocabulary -> No user found with id {}", vocabularyRequest.userId());

                                return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(
                                        ApiResponse.builder()
                                                .code(ApiResponseCode.ERROR.getCode())
                                                .message("No user found with userId: " + vocabularyRequest.userId())
                                                .data(null)
                                                .build()
                                );
                            }));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Method::addVocabulary -> Empty request body");

                    return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(
                            ApiResponse.builder()
                                    .code(ApiResponseCode.ERROR.getCode())
                                    .message("Invalid vocabulary data")
                                    .data(null)
                                    .build()
                    );
                }))
                .onErrorResume(ConstraintViolationException.class, ex ->
                        ServerResponse.badRequest().bodyValue(
                                ApiResponse.builder()
                                        .code(ApiResponseCode.ERROR.getCode())
                                        .message(ex.getConstraintViolations().iterator().next().getMessage())
                                        .data(null)
                                        .build()
                        )
                );
    }

    public Mono<ServerResponse> updateVocabularyById(ServerRequest request) {
        String vocabularyId = request.pathVariable("vocabularyId");

        return request.bodyToMono(Vocabulary.class)
                .flatMap(vocabulary -> {
                    vocabulary.setId(vocabularyId);
                    return vocabularyService.updateVocabulary(vocabulary)
                            .flatMap(updatedVocabulary -> ServerResponse.ok().bodyValue(
                                            ApiResponse.<VocabularyDto>builder()
                                                    .code(ApiResponseCode.SUCCESS.getCode())
                                                    .message("Vocabulary updated successfully")
                                                    .data(vocabularyMapper.toDto(updatedVocabulary))
                                                    .build()
                            ))
                            .switchIfEmpty(Mono.defer(() -> {
                                log.info("Method::updateVocabularyById -> No vocabulary found with id {}", vocabularyId);

                                return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(
                                        ApiResponse.builder()
                                                .code(ApiResponseCode.ERROR.getCode())
                                                .message("No vocabulary found with id: " + vocabularyId)
                                                .data(null)
                                                .build()
                                );
                            }));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Method::updateVocabularyById -> Empty request body");

                    return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(
                            ApiResponse.builder()
                                    .code(ApiResponseCode.ERROR.getCode())
                                    .message("Invalid vocabulary data")
                                    .data(null)
                                    .build()
                    );
                }));
    }

    public Mono<ServerResponse> deleteVocabularyById(ServerRequest request) {
        String vocabularyId = request.pathVariable("vocabularyId");

        return vocabularyService.getVocabularyById(vocabularyId)
                .flatMap(vocabulary ->
                        vocabularyService.deleteVocabularyById(vocabularyId)
                        .then(Mono.defer(() -> ServerResponse.ok().bodyValue(
                                ApiResponse.<VocabularyDto>builder()
                                        .code(ApiResponseCode.SUCCESS.getCode())
                                        .message("Vocabulary " + vocabulary.getWord() + " has been deleted successfully")
                                        .data(vocabularyMapper.toDto(vocabulary))
                                        .build()
                        )))
                )
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Method::deleteVocabularyById -> No vocabulary found with id {}", vocabularyId);

                    return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(
                            ApiResponse.builder()
                                    .code(ApiResponseCode.ERROR.getCode())
                                    .message("No vocabulary found with id: " + vocabularyId)
                                    .data(null)
                                    .build()
                    );
                }));
    }
}
