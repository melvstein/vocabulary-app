package dev.melvstein.vocabulary_app.router;

import dev.melvstein.vocabulary_app.handler.AdminUserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AdminUserRouter {

    @Bean
    public RouterFunction<ServerResponse> adminUsersRoute(AdminUserHandler adminUserHandler) {
        String ADMIN_USERS_ENDPOINT = "api/admin/users";

        return RouterFunctions
                .route()
                .GET(ADMIN_USERS_ENDPOINT, adminUserHandler::getAllAdminUsers)
                .GET(ADMIN_USERS_ENDPOINT + "/{adminUserId}", adminUserHandler::getAdminUserById)
                .POST(ADMIN_USERS_ENDPOINT, adminUserHandler::createAdminUser)
                .PATCH(ADMIN_USERS_ENDPOINT + "/{adminUserId}", adminUserHandler::updateAdminUserById)
                .DELETE(ADMIN_USERS_ENDPOINT + "/{adminUserId}", adminUserHandler::deleteAdminUserById)
                .build();
    }
}
