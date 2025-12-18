package dev.melvstein.vocabulary_app.handler;

import dev.melvstein.vocabulary_app.Dto.ApiResponse;
import dev.melvstein.vocabulary_app.Dto.UserDto;
import dev.melvstein.vocabulary_app.Dto.VocabularyDto;
import dev.melvstein.vocabulary_app.enums.ApiResponseCode;
import dev.melvstein.vocabulary_app.mapper.UserMapper;
import dev.melvstein.vocabulary_app.model.User;
import dev.melvstein.vocabulary_app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserHandler {
    private final UserService userService;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return userService.getAllUsers()
                .map(userMapper::toDto)
                .collectList()
                .flatMap(users -> ServerResponse.ok().bodyValue(
                        ApiResponse.<List<UserDto>>builder()
                                .code(ApiResponseCode.SUCCESS.getCode())
                                .message(ApiResponseCode.SUCCESS.getMessage())
                                .data(users)
                                .build()
                ));
    }

    public Mono<ServerResponse> getUserById(ServerRequest request) {
        String userId = request.pathVariable("userId");

        return userService.getUserById(userId)
                .map(userMapper::toDto)
                .flatMap(userDto -> ServerResponse.ok().bodyValue(
                        ApiResponse.<UserDto>builder()
                                .code(ApiResponseCode.SUCCESS.getCode())
                                .message(ApiResponseCode.SUCCESS.getMessage())
                                .data(userDto)
                                .build()
                ))
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Method::getUserById -> No user found with id {}", userId);

                    return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(
                            ApiResponse.<UserDto>builder()
                                    .code(ApiResponseCode.ERROR.getCode())
                                    .message("No user found with userId: " + userId)
                                    .data(null)
                                    .build()
                    );
                }));
    }

    public Mono<ServerResponse> saveUser(ServerRequest request) {
        return request.bodyToMono(User.class)
                .flatMap(userRequest ->
                        {
                            log.info("Received request to save user: {}", objectMapper.writeValueAsString(userRequest));

                            // find the user first
                            return userService.getUserByUsername(userRequest.getUsername())
                                    .map(userMapper::toDto)
                                    .flatMap(existingUser -> {
                                            log.info("Method::saveUser -> Existing user: {}", existingUser);

                                            return ServerResponse.status(HttpStatus.CONFLICT).bodyValue(
                                                    ApiResponse.<UserDto>builder()
                                                            .code(ApiResponseCode.ERROR.getCode())
                                                            .message("User with username " + existingUser.username() + " already exists")
                                                            .data(existingUser)
                                                            .build()
                                            );
                                    })
                                    .switchIfEmpty(
                                            // User does not exist, save it
                                            userService.saveUser(userRequest)
                                                    .map(userMapper::toDto)
                                                    .flatMap(savedUser -> ServerResponse.ok().bodyValue(
                                                            ApiResponse.<UserDto>builder()
                                                                    .code(ApiResponseCode.SUCCESS.getCode())
                                                                    .message(ApiResponseCode.SUCCESS.getMessage())
                                                                    .data(savedUser)
                                                                    .build()
                                                    ))
                                    );
                        }
                );
    }

    public Mono<ServerResponse> updateUser(ServerRequest request) {
        String userId = request.pathVariable("userId");

        return request.bodyToMono(User.class)
                .flatMap(user -> {
                    log.info("Received request to update user: {}", objectMapper.writeValueAsString(user));

                    return userService.getUserById(userId)
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
                                        .flatMap(updatedUser -> ServerResponse.ok().bodyValue(
                                                ApiResponse.<UserDto>builder()
                                                        .code(ApiResponseCode.SUCCESS.getCode())
                                                        .message(ApiResponseCode.SUCCESS.getMessage())
                                                        .data(updatedUser)
                                                        .build()
                                        ));
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                log.info("Method::updateUser -> No user found with userId: {}", userId);

                                return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(
                                        ApiResponse.<UserDto>builder()
                                                .code(ApiResponseCode.ERROR.getCode())
                                                .message("No user found with userId: " + userId)
                                                .data(null)
                                                .build()
                                );
                            }));
                });
    }

    public Mono<ServerResponse> deleteUserById(ServerRequest request) {
        String userId = request.pathVariable("userId");

        return userService.deleteUserById(userId)
                .map(userMapper::toDto)
                .flatMap(deletedUser -> ServerResponse.ok().bodyValue(
                        ApiResponse.<UserDto>builder()
                                .code(ApiResponseCode.SUCCESS.getCode())
                                .message(ApiResponseCode.SUCCESS.getMessage())
                                .data(deletedUser)
                                .build()
                ))
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Method::deleteUserById -> No user found with userId: {}", userId);

                    return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(
                            ApiResponse.<UserDto>builder()
                                    .code(ApiResponseCode.ERROR.getCode())
                                    .message("No user found with userId: " + userId)
                                    .data(null)
                                    .build()
                    );
                }));
    }
}
