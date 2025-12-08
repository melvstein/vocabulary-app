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
        return RouterFunctions
                .route()
                .GET("api/users", userHandler::getAllUsers)
                .POST("api/users", userHandler::saveUser)
                .build();
    }
}
