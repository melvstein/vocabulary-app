package dev.melvstein.vocabulary_app.service;

import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public abstract class BaseService {
    protected final PasswordEncoder passwordEncoder;

    public BaseService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
