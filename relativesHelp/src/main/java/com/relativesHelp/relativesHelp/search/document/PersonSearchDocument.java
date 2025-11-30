package com.relativesHelp.relativesHelp.search.document;

import com.relativesHelp.relativesHelp.family.entity.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(indexName = "person_index")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonSearchDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Long)
    private Long familyTreeId;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Text)
    private String fullName;

    @Field(type = FieldType.Keyword)
    private String gender;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate dateOfBirth;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate dateOfDeath;

    @Field(type = FieldType.Text)
    private String placeOfBirth;

    @Field(type = FieldType.Text)
    private String placeOfDeath;

    @Field(type = FieldType.Boolean)
    private Boolean isAlive;

    @Field(type = FieldType.Text)
    private String biography;

    @Field(type = FieldType.Text)
    private String occupation;

    @Field(type = FieldType.Integer)
    private Integer generationLevel;

    @Field(type = FieldType.Text)
    private String avatarUrl;

    @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime updatedAt;

    public static PersonSearchDocument fromPerson(Person person) {
        return PersonSearchDocument.builder()
                .id(person.getId())
                .familyTreeId(person.getFamilyTreeId())
                .userId(person.getUserId())
                .fullName(person.getFullName())
                .gender(person.getGender() != null ? person.getGender().name() : null)
                .dateOfBirth(person.getDateOfBirth())
                .dateOfDeath(person.getDateOfDeath())
                .placeOfBirth(person.getPlaceOfBirth())
                .placeOfDeath(person.getPlaceOfDeath())
                .isAlive(person.getIsAlive())
                .biography(person.getBiography())
                .occupation(person.getOccupation())
                .generationLevel(person.getGenerationLevel())
                .avatarUrl(person.getAvatarUrl())
                .createdAt(person.getCreatedAt())
                .updatedAt(person.getUpdatedAt())
                .build();
    }

    public Person toPersonEntity() {
        return Person.builder()
                .id(this.id)
                .familyTreeId(this.familyTreeId)
                .userId(this.userId)
                .fullName(this.fullName)
                .gender(this.gender != null ? Person.Gender.valueOf(this.gender) : null)
                .dateOfBirth(this.dateOfBirth)
                .dateOfDeath(this.dateOfDeath)
                .placeOfBirth(this.placeOfBirth)
                .placeOfDeath(this.placeOfDeath)
                .isAlive(this.isAlive)
                .biography(this.biography)
                .occupation(this.occupation)
                .generationLevel(this.generationLevel != null ? this.generationLevel : 0)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}


