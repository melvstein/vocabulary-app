package dev.melvstein.vocabulary_app.handler;

import dev.melvstein.vocabulary_app.Dto.UserDto;
import dev.melvstein.vocabulary_app.mapper.UserMapper;
import dev.melvstein.vocabulary_app.model.User;
import dev.melvstein.vocabulary_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {
    private final UserService userService;
    private final UserMapper userMapper;

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return ServerResponse.ok().body(userService.getAllUsers().map(userMapper::toDto), UserDto.class);
    }

    public Mono<ServerResponse> saveUser(ServerRequest request) {
        return request.bodyToMono(User.class)
                .flatMap(userService::saveUser)
                .flatMap(savedUser -> ServerResponse.ok().bodyValue(savedUser));
    }
}
