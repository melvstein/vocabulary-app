package dev.melvstein.vocabulary_app.router;

import dev.melvstein.vocabulary_app.Dto.ApiResponse;
import dev.melvstein.vocabulary_app.config.SecurityProperties;
import dev.melvstein.vocabulary_app.enums.ApiResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

public abstract class BaseRouter {
    public SecurityProperties securityProperties;

    public BaseRouter(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public HandlerFilterFunction<ServerResponse, ServerResponse> apiKeyFilter() {
        return (request, next) -> {

            String apiKey = request.headers().firstHeader("X-API-KEY");

            if (apiKey == null) {
                return ServerResponse
                        .status(HttpStatus.UNAUTHORIZED)
                        .bodyValue(
                                ApiResponse.builder()
                                        .code(ApiResponseCode.ERROR.getCode())
                                        .message("Missing X-API-KEY header.")
                                        .build()
                        );
            }

            if (!apiKey.equals(securityProperties.getApiKey())) {
                return ServerResponse
                        .status(HttpStatus.FORBIDDEN)
                        .bodyValue(
                                ApiResponse.builder()
                                        .code(ApiResponseCode.ERROR.getCode())
                                        .message("Invalid API key.")
                                        .build()
                        );
            }

            return next.handle(request);
        };
    }
}
