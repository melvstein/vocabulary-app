package dev.melvstein.vocabulary_app.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.melvstein.vocabulary_app.enums.Role;
import dev.melvstein.vocabulary_app.validation.ValidEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AdminUserDto(
    String id,

    @NotBlank(message = "Required role")
    @Pattern(regexp = "ADMIN|STAFF", message = "Invalid role. Allowed values are ADMIN or STAFF")
    String role,

    @NotBlank(message = "Required firstName")
    String firstName,

    @NotBlank(message = "Required middleName")
    String middleName,

    @NotBlank(message = "Required lastName")
    String lastName,

    @NotBlank(message = "Required username")
    String username,

    @NotBlank(message = "Required email")
    @Email(message = "Invalid email format")
    String email,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt
) {
}
