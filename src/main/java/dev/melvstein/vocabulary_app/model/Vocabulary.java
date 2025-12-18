package dev.melvstein.vocabulary_app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "vocabularies")
@CompoundIndexes({
        @CompoundIndex(
                name = "user_word_idx",
                def = "{'userId': 1, 'word': 1}",
                unique = true
        )
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vocabulary {
    @Id
    private String id;

    private String userId;
    private String word;
    private String partOfSpeech;
    private String englishDefinition;
    private String tagalogDefinition;
    private String englishSynonyms;
    private String tagalogSynonyms;
    private String englishAntonyms;
    private String tagalogAntonyms;
    private String exampleSentence;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
