package dev.melvstein.vocabulary_app.router;

import dev.melvstein.vocabulary_app.Dto.ApiResponse;
import dev.melvstein.vocabulary_app.config.SecurityProperties;
import dev.melvstein.vocabulary_app.enums.ApiResponseCode;
import dev.melvstein.vocabulary_app.handler.UserHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class UserRouter extends BaseRouter {

    public UserRouter(SecurityProperties securityProperties) {
        super(securityProperties);
    }

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler userHandler) {
        String USERS_ENDPOINT = "/api/users";

        return RouterFunctions
                .route()
                .filter(apiKeyFilter())
                .GET(USERS_ENDPOINT, userHandler::getAllUsers)
                .GET(USERS_ENDPOINT + "/{userId}", userHandler::getUserById)
                .POST(USERS_ENDPOINT, userHandler::saveUser)
                .PATCH(USERS_ENDPOINT + "/{userId}", userHandler::updateUser)
                .DELETE(USERS_ENDPOINT + "/{userId}", userHandler::deleteUserById)
                .build();
    }
}
