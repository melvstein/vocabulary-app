package dev.melvstein.vocabulary_app.handler;

import dev.melvstein.vocabulary_app.Dto.UserDto;
import dev.melvstein.vocabulary_app.mapper.UserMapper;
import dev.melvstein.vocabulary_app.model.User;
import dev.melvstein.vocabulary_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {
    private final UserService userService;
    private final UserMapper userMapper;

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return ServerResponse.ok().body(userService.getAllUsers().map(userMapper::toDto), UserDto.class);
    }

    public Mono<ServerResponse> getUserById(ServerRequest request) {
        String id = request.pathVariable("id");

        return userService.getUserById(id)
                .map(userMapper::toDto)
                .flatMap(dto -> ServerResponse.ok().bodyValue(dto))
                .switchIfEmpty(Mono.defer(() -> {
                    System.out.println("No user found with id: " + id);
                    return ServerResponse.notFound().build();
                }));
    }

    public Mono<ServerResponse> saveUser(ServerRequest request) {
        return request.bodyToMono(User.class)
                .flatMap(user ->
                        userService.getUserByUsername(user.getUsername())
                                .flatMap(existingUser -> {
                                    // User exists, return an error or log
                                    System.out.println("User with id " + existingUser.getId() + " already exists.");
                                    return ServerResponse.badRequest()
                                            .bodyValue("User already exists");
                                })
                                .switchIfEmpty(
                                        // User does not exist, save it
                                        userService.saveUser(user)
                                                .map(userMapper::toDto)
                                                .flatMap(dto -> ServerResponse.ok().bodyValue(dto))
                                )
                );
    }

    public Mono<ServerResponse> updateUser(ServerRequest request) {
        String id = request.pathVariable("id");

        return request.bodyToMono(User.class)
                .flatMap(user ->
                    userService.getUserById(id)
                        .flatMap(existingUser -> {
                            // Update fields
                            if (user.getRole() != null) {
                                existingUser.setRole(user.getRole());
                            }

                            if (user.getUsername() != null) {
                                existingUser.setUsername(user.getUsername());
                            }

                            if (user.getPassword() != null) {
                                existingUser.setPassword(user.getPassword());
                                existingUser.setEncryptedPassword(userService
                                        .getPasswordEncoder()
                                        .encode(user.getPassword())
                                );
                            }

                            if (user.getEmail() != null) {
                                existingUser.setEmail(user.getEmail());
                            }

                            if (user.getFirstName() != null) {
                                existingUser.setFirstName(user.getFirstName());
                            }

                            if (user.getMiddleName() != null) {
                                existingUser.setMiddleName(user.getMiddleName());
                            }

                            if (user.getLastName() != null) {
                                existingUser.setLastName(user.getLastName());
                            }

                            // Save updated user
                            return userService.saveUser(existingUser)
                                    .map(userMapper::toDto)
                                    .flatMap(dto -> ServerResponse.ok().bodyValue(dto));
                        })
                        .switchIfEmpty(ServerResponse.notFound().build())
                );
    }

    public Mono<ServerResponse> deleteUserById(ServerRequest request) {
        String id = request.pathVariable("id");

        return userService.deleteUserById(id)
                .map(userMapper::toDto)
                .flatMap(deletedUser -> ServerResponse.ok().bodyValue(deletedUser))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
