package dev.melvstein.vocabulary_app.router;

import dev.melvstein.vocabulary_app.handler.VocabularyHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class VocabularyRouter {

    @Bean
    public RouterFunction<ServerResponse> vocabularyRoutes(VocabularyHandler vocabularyHandler) {
        String VOCABULARY_ENDPOINT = "api/vocabularies";

        return RouterFunctions
                .route()
                .GET(VOCABULARY_ENDPOINT, vocabularyHandler::getVocabularies)
                .GET(VOCABULARY_ENDPOINT + "/user/{userId}", vocabularyHandler::getVocabulariesByUserId)
                .POST(VOCABULARY_ENDPOINT, vocabularyHandler::addVocabulary)
                .DELETE(VOCABULARY_ENDPOINT + "/{vocabularyId}", vocabularyHandler::deleteVocabularyById)
                .build();
    }
}
