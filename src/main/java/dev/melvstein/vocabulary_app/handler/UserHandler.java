package dev.melvstein.vocabulary_app.handler;

import dev.melvstein.vocabulary_app.Dto.ApiResponse;
import dev.melvstein.vocabulary_app.Dto.UserDto;
import dev.melvstein.vocabulary_app.enums.ApiResponseCode;
import dev.melvstein.vocabulary_app.mapper.UserMapper;
import dev.melvstein.vocabulary_app.model.User;
import dev.melvstein.vocabulary_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserHandler {
    private final UserService userService;
    private final UserMapper userMapper;

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        ApiResponse<List<UserDto>> response = ApiResponse.<List<UserDto>>builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseCode.SUCCESS.getMessage())
                .data(List.of())
                .build();

        return userService.getAllUsers()
                .map(userMapper::toDto)
                .collectList()
                .flatMap(users -> {
                    response.setData(users);
                    return ServerResponse.ok().bodyValue(response);
                });
    }

    public Mono<ServerResponse> getUserById(ServerRequest request) {
        String id = request.pathVariable("id");

        return userService.getUserById(id)
                .map(userMapper::toDto)
                .flatMap(userDto -> {
                    System.out.println("Found user: " + userDto);
                    return ServerResponse.ok().bodyValue(userDto);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    System.out.println("No user found with id: " + id);
                    return ServerResponse.notFound().build();
                }));
    }

    public Mono<ServerResponse> saveUser(ServerRequest request) {
        return request.bodyToMono(User.class)
                .flatMap(userRequest ->
                        // find the user first
                        userService.getUserByUsername(userRequest.getUsername())
                                .flatMap(existingUser -> {
                                    System.out.println("User with id " + existingUser.getId() + " already exists.");
                                    return ServerResponse.badRequest().build();
                                })
                                .switchIfEmpty(
                                        // User does not exist, save it
                                        userService.saveUser(userRequest)
                                                .map(userMapper::toDto)
                                                .flatMap(savedUser -> {
                                                    System.out.println("Saved new user with id " + savedUser.id());
                                                    return ServerResponse.ok().bodyValue(savedUser);
                                                })
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
                                    .flatMap(updatedUser -> ServerResponse.ok().bodyValue(updatedUser));
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            System.out.println("No user found with id: " + id);
                            return ServerResponse.notFound().build();
                        }))
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
