package dev.melvstein.vocabulary_app.service;

import dev.melvstein.vocabulary_app.model.AdminUser;
import dev.melvstein.vocabulary_app.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AdminUserService extends BaseService {
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public Flux<AdminUser> getAllAdminUsers() {
        return adminUserRepository.findAll();
    }

    public Mono<AdminUser> getAdminUserById(String id) {
        return adminUserRepository.findById(id);
    }

    public Mono<AdminUser> getAdminUserByUsername(String username) {
        return adminUserRepository.findByUsername(username);
    }

    public Mono<AdminUser> getAdminUserByEmail(String email) {
        return adminUserRepository.findByEmail(email);
    }

    public Mono<AdminUser> saveAdminUser(AdminUser adminUser) {
        return adminUserRepository.save(adminUser);
    }

    public Mono<AdminUser> updateAdminUser(AdminUser adminUser) {
        return adminUserRepository.findById(adminUser.getId())
                .flatMap(existingAdminUser -> {
                    if (adminUser.getRole() != null) {
                        existingAdminUser.setRole(adminUser.getRole());
                    }

                    if (adminUser.getFirstName() != null) {
                        existingAdminUser.setFirstName(adminUser.getFirstName());
                    }

                    if (adminUser.getMiddleName() != null) {
                        existingAdminUser.setMiddleName(adminUser.getMiddleName());
                    }

                    if (adminUser.getLastName() != null) {
                        existingAdminUser.setLastName(adminUser.getLastName());
                    }

                    if (adminUser.getUsername() != null) {
                        existingAdminUser.setUsername(adminUser.getUsername());
                    }

                    if (adminUser.getPassword() != null) {
                        existingAdminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
                        existingAdminUser.setEncryptedPassword(passwordEncoder.encode(adminUser.getPassword()));
                    }

                    if (adminUser.getEmail() != null) {
                        existingAdminUser.setEmail(adminUser.getEmail());
                    }

                    return adminUserRepository.save(existingAdminUser);
                });
    }

    public Mono<AdminUser> deleteAdminUserById(String id) {
        return adminUserRepository.findById(id)
                .flatMap(adminUser ->
                        adminUserRepository.delete(adminUser)
                                .then(Mono.just(adminUser))
                );
    }
}
