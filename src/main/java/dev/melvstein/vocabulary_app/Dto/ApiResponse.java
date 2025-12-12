package dev.melvstein.vocabulary_app.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {
    private String code;
    private String message;
    private T data;
}
