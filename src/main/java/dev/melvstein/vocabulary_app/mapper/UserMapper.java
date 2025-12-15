package dev.melvstein.vocabulary_app.mapper;

import dev.melvstein.vocabulary_app.Dto.UserDto;
import dev.melvstein.vocabulary_app.Dto.VocabularyDto;
import dev.melvstein.vocabulary_app.model.User;
import dev.melvstein.vocabulary_app.model.Vocabulary;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public VocabularyDto toVocabularyDto(Vocabulary vocabulary) {
        if (vocabulary == null) {
            return null;
        }

        return VocabularyDto.builder()
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
}
