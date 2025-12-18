package dev.melvstein.vocabulary_app.handler;

import dev.melvstein.vocabulary_app.Dto.ApiResponse;
import dev.melvstein.vocabulary_app.Dto.UserDto;
import dev.melvstein.vocabulary_app.Dto.VocabularyDto;
import dev.melvstein.vocabulary_app.enums.ApiResponseCode;
import dev.melvstein.vocabulary_app.mapper.VocabularyMapper;
import dev.melvstein.vocabulary_app.service.VocabularyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VocabularyHandler {
    private final VocabularyService vocabularyService;
    private final VocabularyMapper vocabularyMapper;

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

        return vocabularyService.getVocabulariesByUserId(userId)
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
                })
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest().bodyValue(
                                ApiResponse.builder()
                                        .code(ApiResponseCode.ERROR.getCode())
                                        .message(e.getMessage())
                                        .data(null)
                                        .build()
                        )
                );
    }

    public Mono<ServerResponse> addVocabulary(ServerRequest request) {
        return request.bodyToMono(VocabularyDto.class)
                .map(vocabularyMapper::toModel)
                .flatMap(vocabularyService::addVocabulary)
                .map(saved -> ApiResponse.<VocabularyDto>builder()
                        .code(ApiResponseCode.SUCCESS.getCode())
                        .message("Vocabulary added successfully.")
                        .data(vocabularyMapper.toDto(saved))
                        .build()
                )
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest().bodyValue(
                                ApiResponse.builder()
                                        .code(ApiResponseCode.ERROR.getCode())
                                        .message(e.getMessage())
                                        .data(null)
                                        .build()
                        )
                );
    }
}
