package dev.melvstein.vocabulary_app.mapper;

import dev.melvstein.vocabulary_app.Dto.AdminUserDto;
import dev.melvstein.vocabulary_app.model.AdminUser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminUserMapper {

    public AdminUserDto toDto(AdminUser adminUser) {
        if (adminUser == null) {
            return null;
        }

        return AdminUserDto.builder()
                .id(adminUser.getId())
                .role(adminUser.getRole())
                .firstName(adminUser.getFirstName())
                .middleName(adminUser.getMiddleName())
                .lastName(adminUser.getLastName())
                .username(adminUser.getUsername())
                .email(adminUser.getEmail())
                .createdAt(adminUser.getCreatedAt())
                .updatedAt(adminUser.getUpdatedAt())
                .build();
    }

    public AdminUser toDocument(AdminUserDto adminUserDto) {
        if (adminUserDto == null) {
            return null;
        }

        return AdminUser.builder()
                .id(adminUserDto.id())
                .role(adminUserDto.role())
                .firstName(adminUserDto.firstName())
                .middleName(adminUserDto.middleName())
                .lastName(adminUserDto.lastName())
                .username(adminUserDto.username())
                .email(adminUserDto.email())
                .createdAt(adminUserDto.createdAt())
                .updatedAt(adminUserDto.updatedAt())
                .build();
    }

    public List<AdminUserDto> toDtos(List<AdminUser> adminUsers) {
        if (adminUsers == null) {
            return null;
        }

        return adminUsers.stream()
                .map(this::toDto)
                .toList();
    }
}
