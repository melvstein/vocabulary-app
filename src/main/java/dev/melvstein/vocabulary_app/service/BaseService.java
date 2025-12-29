package dev.melvstein.vocabulary_app.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.Set;

public abstract class BaseService {
    @Autowired
    protected Validator validator;

    public <T> Mono<T> validateRequest(T request) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            return Mono.error(new ConstraintViolationException(violations));
        }

        return Mono.just(request);
    }
}
