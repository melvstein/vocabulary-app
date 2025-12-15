package dev.melvstein.vocabulary_app.router;

import dev.melvstein.vocabulary_app.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler userHandler) {
        String USERS_ENDPOINT = "api/users";

        return RouterFunctions
                .route()
                .GET(USERS_ENDPOINT, userHandler::getAllUsers)
                .GET(USERS_ENDPOINT + "/{userId}", userHandler::getUserById)
                .GET(USERS_ENDPOINT + "/{userId}/vocabularies", userHandler::getVocabulariesByUserId)
                .POST(USERS_ENDPOINT, userHandler::saveUser)
                .PUT(USERS_ENDPOINT + "/{userId}", userHandler::updateUser)
                .DELETE(USERS_ENDPOINT + "/{userId}", userHandler::deleteUserById)
                .build();
    }
}
