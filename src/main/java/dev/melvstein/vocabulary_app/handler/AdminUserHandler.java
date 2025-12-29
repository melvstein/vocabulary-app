package dev.melvstein.vocabulary_app.handler;

import dev.melvstein.vocabulary_app.Dto.AdminUserDto;
import dev.melvstein.vocabulary_app.Dto.ApiResponse;
import dev.melvstein.vocabulary_app.Dto.VocabularyDto;
import dev.melvstein.vocabulary_app.enums.ApiResponseCode;
import dev.melvstein.vocabulary_app.mapper.AdminUserMapper;
import dev.melvstein.vocabulary_app.model.AdminUser;
import dev.melvstein.vocabulary_app.service.AdminUserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserHandler {
    private final AdminUserService adminUserService;
    private final AdminUserMapper adminUserMapper;
    private final Validator validator;

    public Mono<ServerResponse> getAllAdminUsers(ServerRequest serverRequest) {
        return adminUserService.getAllAdminUsers()
                .collectList()
                .flatMap(adminUsers -> ServerResponse.ok().bodyValue(ApiResponse.<List<AdminUserDto>>builder()
                        .code(ApiResponseCode.SUCCESS.getCode())
                        .message(ApiResponseCode.SUCCESS.getMessage())
                        .data(adminUserMapper.toDtos(adminUsers))
                        .build()));
    }

    public Mono<ServerResponse> getAdminUserById(ServerRequest serverRequest) {
        String adminUserId = serverRequest.pathVariable("adminUserId");

        return adminUserService.getAdminUserById(adminUserId)
                .flatMap(adminUser -> ServerResponse.ok().bodyValue(ApiResponse.<AdminUserDto>builder()
                        .code(ApiResponseCode.SUCCESS.getCode())
                        .message(ApiResponseCode.SUCCESS.getMessage())
                        .data(adminUserMapper.toDto(adminUser))
                        .build()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Method::getAdminUserById -> No admin user found with id {}", adminUserId);

                    return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(ApiResponse.<AdminUserDto>builder()
                            .code(ApiResponseCode.ERROR.getCode())
                            .message("No admin user found with adminUserId: " + adminUserId)
                            .data(null)
                            .build());
                }));
    }

    public Mono<ServerResponse> createAdminUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(AdminUserDto.class)
                .flatMap(adminUserService::validateRequest)
                .flatMap(adminUserRequest -> adminUserService.getAdminUserByUsername(adminUserRequest.username())
                        .flatMap(existingAdminUser -> {
                            log.info("Method::createAdminUser -> Admin user with username {} already exists", adminUserRequest.username());
                            return ServerResponse.status(HttpStatus.CONFLICT).bodyValue(ApiResponse.<AdminUserDto>builder()
                                    .code(ApiResponseCode.ERROR.getCode())
                                    .message("Admin user with username " + adminUserRequest.username() + " already exists")
                                    .data(null)
                                    .build());
                        })
                        .switchIfEmpty(Mono.defer(() -> adminUserService.getAdminUserByEmail(adminUserRequest.email())
                                .flatMap(existingAdminUser -> {
                                    log.info("Method::createAdminUser -> Admin user with email {} already exists", adminUserRequest.email());
                                    return ServerResponse.status(HttpStatus.CONFLICT).bodyValue(ApiResponse.<AdminUserDto>builder()
                                            .code(ApiResponseCode.ERROR.getCode())
                                            .message("Admin user with email " + adminUserRequest.email() + " already exists")
                                            .data(null)
                                            .build());
                                })
                                .switchIfEmpty(Mono.defer(() -> adminUserService.saveAdminUser(adminUserMapper.toDocument(adminUserRequest))
                                        .flatMap(savedAdminUser -> ServerResponse.status(HttpStatus.CREATED).bodyValue(ApiResponse.<AdminUserDto>builder()
                                                .code(ApiResponseCode.SUCCESS.getCode())
                                                .message("Admin user created successfully")
                                                .data(adminUserMapper.toDto(savedAdminUser))
                                                .build()))
                                ))
                        ))
                )
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Method::createAdminUser -> Empty request body");
                    return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(
                            ApiResponse.builder()
                                    .code(ApiResponseCode.ERROR.getCode())
                                    .message("Invalid admin user data")
                                    .data(null)
                                    .build()
                    );
                }))
                .onErrorResume(ConstraintViolationException.class, ex -> {
                    log.info("Method::createAdminUser -> Validation error", ex);
                    return ServerResponse.badRequest().bodyValue(
                            ApiResponse.builder()
                                    .code(ApiResponseCode.ERROR.getCode())
                                    .message(ex.getConstraintViolations().iterator().next().getMessage())
                                    .data(null)
                                    .build()
                    );
                })
                .onErrorResume(Exception.class, ex -> {
                    log.info("Method::createAdminUser -> Deserialization or other error", ex);
                    return ServerResponse.badRequest().bodyValue(
                            ApiResponse.builder()
                                    .code(ApiResponseCode.ERROR.getCode())
                                    .message("Invalid request: " + ex.getMessage())
                                    .data(null)
                                    .build()
                    );
                });
    }

    public Mono<ServerResponse> updateAdminUserById(ServerRequest serverRequest) {
        String adminUserId = serverRequest.pathVariable("adminUserId");

        return serverRequest.bodyToMono(AdminUser.class)
                .flatMap(adminUserService::validateRequest)
                .flatMap(adminUserRequest -> {
                    adminUserRequest.setId(adminUserId);
                    return adminUserService.updateAdminUser(adminUserRequest)
                            .flatMap(updatedAdminUser -> ServerResponse.ok().bodyValue(
                                    ApiResponse.<AdminUserDto>builder()
                                            .code(ApiResponseCode.SUCCESS.getCode())
                                            .message("Admin user updated successfully")
                                            .data(adminUserMapper.toDto(updatedAdminUser))
                                            .build()
                            ))
                            .switchIfEmpty(Mono.defer(() -> {
                                log.info("Method::updateAdminUserById -> No admin user found with id {}", adminUserId);
                                return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(
                                        ApiResponse.builder()
                                                .code(ApiResponseCode.ERROR.getCode())
                                                .message("No admin user found with adminUserId: " + adminUserId)
                                                .data(null)
                                                .build()
                                );
                            }));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Method::updateAdminUserById -> Empty request body");
                    return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(
                            ApiResponse.builder()
                                    .code(ApiResponseCode.ERROR.getCode())
                                    .message("Invalid admin user data")
                                    .data(null)
                                    .build()
                    );
                }))
                .onErrorResume(ConstraintViolationException.class, ex -> {
                    log.info("Method::updateAdminUserById -> Validation error", ex);
                    return ServerResponse.badRequest().bodyValue(
                            ApiResponse.builder()
                                    .code(ApiResponseCode.ERROR.getCode())
                                    .message(ex.getConstraintViolations().iterator().next().getMessage())
                                    .data(null)
                                    .build()
                    );
                })
                .onErrorResume(Exception.class, ex -> {
                    log.info("Method::updateAdminUserById -> Deserialization or other error", ex);
                    return ServerResponse.badRequest().bodyValue(
                            ApiResponse.builder()
                                    .code(ApiResponseCode.ERROR.getCode())
                                    .message("Invalid request: " + ex.getMessage())
                                    .data(null)
                                    .build()
                    );
                });
    }

    public Mono<ServerResponse> deleteAdminUserById(ServerRequest serverRequest) {
        String adminUserId = serverRequest.pathVariable("adminUserId");

        return adminUserService.deleteAdminUserById(adminUserId)
                .flatMap(deletedUser -> {
                    log.info("Method::deleteAdminUserById -> Admin user with id {} deleted successfully", adminUserId);
                    return ServerResponse.ok().bodyValue(
                            ApiResponse.builder()
                                    .code(ApiResponseCode.SUCCESS.getCode())
                                    .message("Admin user deleted successfully")
                                    .data(null)
                                    .build()
                    );
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Method::deleteAdminUserById -> No admin user found with id {}", adminUserId);

                    return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(
                            ApiResponse.builder()
                                    .code(ApiResponseCode.ERROR.getCode())
                                    .message("No admin user found with adminUserId: " + adminUserId)
                                    .data(null)
                                    .build()
                    );
                }));
    }
}
