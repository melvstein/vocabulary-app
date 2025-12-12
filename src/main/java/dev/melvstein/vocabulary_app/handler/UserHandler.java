package dev.melvstein.vocabulary_app.handler;

import dev.melvstein.vocabulary_app.Dto.ApiResponse;
import dev.melvstein.vocabulary_app.Dto.UserDto;
import dev.melvstein.vocabulary_app.enums.ApiResponseCode;
import dev.melvstein.vocabulary_app.mapper.UserMapper;
import dev.melvstein.vocabulary_app.model.User;
import dev.melvstein.vocabulary_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                .code(ApiResponseCode.ERROR.getCode())
                .message("Internal Server Error")
                .data(null)
                .build();

        String id = request.pathVariable("id");

        return userService.getUserById(id)
                .map(userMapper::toDto)
                .flatMap(userDto -> {
                    System.out.println("Found user: " + userDto);
                    response.setCode(ApiResponseCode.SUCCESS.getCode());
                    response.setMessage(ApiResponseCode.SUCCESS.getMessage());
                    response.setData(userDto);
                    return ServerResponse.ok().bodyValue(response);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    System.out.println("No user found with id: " + id);
                    response.setMessage("User not found");
                    return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(response);
                }));
    }

    public Mono<ServerResponse> saveUser(ServerRequest request) {
        ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                .code(ApiResponseCode.ERROR.getCode())
                .message("Internal Server Error")
                .data(null)
                .build();

        return request.bodyToMono(User.class)
                .flatMap(userRequest ->
                        // find the user first
                        userService.getUserByUsername(userRequest.getUsername())
                                .map(userMapper::toDto)
                                .flatMap(existingUser -> {
                                    System.out.println("User with id " + existingUser.id() + " already exists.");
                                    response.setMessage("User with id " + existingUser.id() + " already exists.");
                                    response.setData(existingUser);
                                    return ServerResponse.status(HttpStatus.CONFLICT).bodyValue(response);
                                })
                                .switchIfEmpty(
                                        // User does not exist, save it
                                        userService.saveUser(userRequest)
                                                .map(userMapper::toDto)
                                                .flatMap(savedUser -> {
                                                    System.out.println("Saved new user with id " + savedUser.id());
                                                    response.setCode(ApiResponseCode.SUCCESS.getCode());
                                                    response.setMessage(ApiResponseCode.SUCCESS.getMessage());
                                                    response.setData(savedUser);
                                                    return ServerResponse.ok().bodyValue(response);
                                                })
                                )
                );
    }

    public Mono<ServerResponse> updateUser(ServerRequest request) {
        ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                .code(ApiResponseCode.ERROR.getCode())
                .message("Internal Server Error")
                .data(null)
                .build();

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
                                    .flatMap(updatedUser -> {
                                        response.setCode(ApiResponseCode.SUCCESS.getCode());
                                        response.setMessage(ApiResponseCode.SUCCESS.getMessage());
                                        response.setData(updatedUser);
                                        return ServerResponse.ok().bodyValue(response);
                                    });
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            System.out.println("No user found with id: " + id);
                            response.setMessage("User not found");
                            return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(response);
                        }))
                );
    }

    public Mono<ServerResponse> deleteUserById(ServerRequest request) {
        ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                .code(ApiResponseCode.ERROR.getCode())
                .message("Internal Server Error")
                .data(null)
                .build();

        String id = request.pathVariable("id");

        return userService.deleteUserById(id)
                .map(userMapper::toDto)
                .flatMap(deletedUser -> {
                    response.setCode(ApiResponseCode.SUCCESS.getCode());
                    response.setMessage(ApiResponseCode.SUCCESS.getMessage());
                    response.setData(deletedUser);
                    return ServerResponse.ok().bodyValue(response);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    response.setMessage("No user found with id: " + id);
                    return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(response);
                }));
    }
}
