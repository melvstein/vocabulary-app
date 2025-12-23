package dev.melvstein.vocabulary_app.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record VocabularyDto(
        String id,

        @NotBlank(message = "Required userId")
        String userId,

        @NotBlank(message = "Required word")
        String word,

        @NotBlank(message = "Required partOfSpeech")
        String partOfSpeech,

        @NotBlank(message = "Required englishDefinition")
        String englishDefinition,

        @NotBlank(message = "Required tagalogDefinition")
        String tagalogDefinition,

        @NotBlank(message = "Required englishSynonyms")
        String englishSynonyms,

        @NotBlank(message = "Required tagalogSynonyms")
        String tagalogSynonyms,

        @NotBlank(message = "Required englishAntonyms")
        String englishAntonyms,

        @NotBlank(message = "Required tagalogAntonyms")
        String tagalogAntonyms,

        @NotBlank(message = "Required exampleSentence")
        String exampleSentence,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {
}
