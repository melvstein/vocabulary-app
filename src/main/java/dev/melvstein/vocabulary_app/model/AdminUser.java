package dev.melvstein.vocabulary_app.model;

import dev.melvstein.vocabulary_app.enums.Role;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "admin_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUser {
    @Id
    private String id;

    private String role;

    @Indexed(unique = true)
    private String username;

    private String password;
    private String encryptedPassword;

    @Indexed(unique = true)
    private String email;

    private String firstName;
    private String middleName;
    private String lastName;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
