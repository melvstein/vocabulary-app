package dev.melvstein.vocabulary_app.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record VocabularyDto(
        Long id,
        String word,
        String partOfSpeech,
        String englishDefinition,
        String tagalogDefinition,
        String englishSynonyms,
        String tagalogSynonyms,
        String englishAntonyms,
        String tagalogAntonyms,
        String exampleSentence,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {
}
