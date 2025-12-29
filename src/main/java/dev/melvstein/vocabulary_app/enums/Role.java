package dev.melvstein.vocabulary_app.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ADMIN,
    STAFF;

    @JsonCreator
    public static Role fromString(String value) {
        return value == null ? null : Role.valueOf(value.toUpperCase());
    }
}
