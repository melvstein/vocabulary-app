package dev.melvstein.vocabulary_app.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record UserDto(
        String id,
        String firstName,
        String middleName,
        String lastName,

        @NotBlank(message = "Required username")
        String username,

        @NotBlank(message = "Required email")
        String email,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {
}
