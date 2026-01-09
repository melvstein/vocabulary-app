package dev.melvstein.vocabulary_app.graphql.controller;

import dev.melvstein.vocabulary_app.Dto.AdminUserDto;
import dev.melvstein.vocabulary_app.Dto.ApiResponse;
import dev.melvstein.vocabulary_app.enums.ApiResponseCode;
import dev.melvstein.vocabulary_app.graphql.dto.UpdateAdminUserRequest;
import dev.melvstein.vocabulary_app.mapper.AdminUserMapper;
import dev.melvstein.vocabulary_app.mapper.VocabularyMapper;
import dev.melvstein.vocabulary_app.model.AdminUser;
import dev.melvstein.vocabulary_app.service.AdminUserService;
import dev.melvstein.vocabulary_app.service.VocabularyService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {
    private final AdminUserService adminUserService;
    private final AdminUserMapper adminUserMapper;
    private final VocabularyService vocabularyService;
    private final VocabularyMapper vocabularyMapper;
    private final Validator validator;

    @QueryMapping
    public Mono<ApiResponse<List<AdminUserDto>>> getAllAdminUsers() {
        return adminUserService.getAllAdminUsers()
                .map(adminUserMapper::toDto)
                .collectList()
                .flatMap(adminUsers -> {
                    return Mono.just(ApiResponse.<List<AdminUserDto>>builder()
                            .code(ApiResponseCode.SUCCESS.getCode())
                            .message(ApiResponseCode.SUCCESS.getMessage())
                            .data(adminUsers)
                            .build());
                });
    }

    @QueryMapping
    public Mono<ApiResponse<AdminUserDto>> getAdminUserById(@Argument String id) {
        return adminUserService.getAdminUserById(id)
                .map(adminUserMapper::toDto)
                .flatMap(adminUserDto -> {
                    return Mono.just(ApiResponse.<AdminUserDto>builder()
                            .code(ApiResponseCode.SUCCESS.getCode())
                            .message(ApiResponseCode.SUCCESS.getMessage())
                            .data(adminUserDto)
                            .build()
                    );
                })
                .switchIfEmpty(Mono.just(ApiResponse.<AdminUserDto>builder()
                        .code(ApiResponseCode.SUCCESS.getCode())
                        .message("User not found")
                        .data(null)
                        .build()));
    }

    @MutationMapping
    public Mono<ApiResponse<AdminUserDto>> addAdminUser(@Argument("request") AdminUserDto adminUserDto) {
        return Mono.fromCallable(() -> {
                    Set<ConstraintViolation<AdminUserDto>> violations =
                            validator.validate(adminUserDto);

                    if (!violations.isEmpty()) {
                        throw new ConstraintViolationException(violations);
                    }

                    return adminUserDto;
                })
                .flatMap(validAdminUserDto ->
                        adminUserService
                                .getAdminUserByUsernameOrEmail(
                                        validAdminUserDto.username(),
                                        validAdminUserDto.email()
                                )
                                .flatMap(existingUser ->
                                        Mono.just(ApiResponse.<AdminUserDto>builder()
                                                .code(ApiResponseCode.ERROR.getCode())
                                                .message("Admin user with the given username or email already exists.")
                                                .data(null)
                                                .build()
                                        )
                                )
                                .switchIfEmpty(
                                        adminUserService
                                                .saveAdminUser(
                                                        adminUserMapper.toDocument(validAdminUserDto)
                                                )
                                                .map(savedAdminUser ->
                                                        ApiResponse.<AdminUserDto>builder()
                                                                .code(ApiResponseCode.SUCCESS.getCode())
                                                                .message("Admin user created successfully.")
                                                                .data(adminUserMapper.toDto(savedAdminUser))
                                                                .build()
                                                )
                                )
                )
                .onErrorResume(ConstraintViolationException.class, ex ->
                        Mono.just(ApiResponse.<AdminUserDto>builder()
                                .code(ApiResponseCode.ERROR.getCode())
                                .message(
                                        ex.getConstraintViolations()
                                                .stream()
                                                .map(ConstraintViolation::getMessage)
                                                .reduce((a, b) -> a + ", " + b)
                                                .orElse("Validation error")
                                )
                                .data(null)
                                .build()
                        )
                );
    }

    @MutationMapping
    public Mono<ApiResponse<AdminUserDto>> updateAdminUser(
            @Argument String id,
            @Argument("request") UpdateAdminUserRequest updateAdminUserRequest
    ) {
        AdminUser adminUser = adminUserMapper.toDocument(updateAdminUserRequest);
        adminUser.setId(id);

        return Mono.fromCallable(() -> {
                    Set<ConstraintViolation<UpdateAdminUserRequest>> violations = validator.validate(updateAdminUserRequest);

                    if (!violations.isEmpty()) {
                        throw new ConstraintViolationException(violations);
                    }

                    return updateAdminUserRequest;
                })
                .flatMap(validUpdateRequest ->
                        adminUserService.updateAdminUser(adminUser)
                                .flatMap(updatedAdminUser ->
                                        Mono.just(ApiResponse.<AdminUserDto>builder()
                                                .code(ApiResponseCode.SUCCESS.getCode())
                                                .message("Admin user updated successfully.")
                                                .data(adminUserMapper.toDto(updatedAdminUser))
                                                .build()
                                        )
                                ).switchIfEmpty(Mono.just(ApiResponse.<AdminUserDto>builder()
                                        .code(ApiResponseCode.SUCCESS.getCode())
                                        .message("Admin user not found.")
                                        .data(null)
                                        .build()))
                )
                .onErrorResume(ConstraintViolationException.class, ex ->
                        Mono.just(ApiResponse.<AdminUserDto>builder()
                                .code(ApiResponseCode.ERROR.getCode())
                                .message(
                                        ex.getConstraintViolations()
                                                .stream()
                                                .map(ConstraintViolation::getMessage)
                                                .reduce((a, b) -> a + ", " + b)
                                                .orElse("Validation error")
                                )
                                .data(null)
                                .build()
                        )
                );
    }

    @MutationMapping
    public Mono<ApiResponse<AdminUserDto>> deleteAdminUserById(@Argument String id) {
        return adminUserService.deleteAdminUserById(id)
                .flatMap(deletedAdminUser ->
                        Mono.just(ApiResponse.<AdminUserDto>builder()
                                .code(ApiResponseCode.SUCCESS.getCode())
                                .message("Admin user deleted successfully.")
                                .data(adminUserMapper.toDto(deletedAdminUser))
                                .build()
                        )
                ).switchIfEmpty(Mono.just(ApiResponse.<AdminUserDto>builder()
                        .code(ApiResponseCode.SUCCESS.getCode())
                        .message("Admin user not found.")
                        .data(null)
                        .build()));
    }
}
