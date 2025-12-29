package dev.melvstein.vocabulary_app.mapper;

import dev.melvstein.vocabulary_app.Dto.VocabularyDto;
import dev.melvstein.vocabulary_app.model.Vocabulary;
import org.springframework.stereotype.Component;

@Component
public class VocabularyMapper {

    public VocabularyDto toDto(Vocabulary vocabulary) {
        if (vocabulary == null) {
            return null;
        }

        return VocabularyDto.builder()
                .id(vocabulary.getId())
                .userId(vocabulary.getUserId())
                .word(vocabulary.getWord())
                .partOfSpeech(vocabulary.getPartOfSpeech())
                .englishDefinition(vocabulary.getEnglishDefinition())
                .tagalogDefinition(vocabulary.getTagalogDefinition())
                .englishSynonyms(vocabulary.getEnglishSynonyms())
                .tagalogSynonyms(vocabulary.getTagalogSynonyms())
                .englishAntonyms(vocabulary.getEnglishAntonyms())
                .tagalogAntonyms(vocabulary.getTagalogAntonyms())
                .exampleSentence(vocabulary.getExampleSentence())
                .createdAt(vocabulary.getCreatedAt())
                .updatedAt(vocabulary.getUpdatedAt())
                .build();
    }

    public Vocabulary toDocument(VocabularyDto vocabularyDto) {
        if (vocabularyDto == null) {
            return null;
        }

        return Vocabulary.builder()
                .id(vocabularyDto.id())
                .userId(vocabularyDto.userId())
                .word(vocabularyDto.word())
                .partOfSpeech(vocabularyDto.partOfSpeech())
                .englishDefinition(vocabularyDto.englishDefinition())
                .tagalogDefinition(vocabularyDto.tagalogDefinition())
                .englishSynonyms(vocabularyDto.englishSynonyms())
                .tagalogSynonyms(vocabularyDto.tagalogSynonyms())
                .englishAntonyms(vocabularyDto.englishAntonyms())
                .tagalogAntonyms(vocabularyDto.tagalogAntonyms())
                .exampleSentence(vocabularyDto.exampleSentence())
                .createdAt(vocabularyDto.createdAt())
                .updatedAt(vocabularyDto.updatedAt())
                .build();
    }
}
