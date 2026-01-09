package dev.melvstein.vocabulary_app.graphql.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UpdateAdminUserRequest(
        @Pattern(regexp = "ADMIN|STAFF", message = "Invalid role. Allowed values are ADMIN or STAFF")
        String role,

        String firstName,
        String middleName,
        String lastName,
        String username,
        String password,

        @Email(message = "Invalid email format")
        String email
) {
}
