package com.relativesHelp.relativesHelp.search.document;

import com.relativesHelp.relativesHelp.event.entity.Event;
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

@Document(indexName = "event_index")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSearchDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Long)
    private Long familyTreeId;

    @Field(type = FieldType.Long)
    private Long personId;

    @Field(type = FieldType.Long)
    private Long eventTypeId;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate eventDate;

    @Field(type = FieldType.Keyword)
    private String eventTime;

    @Field(type = FieldType.Boolean)
    private Boolean isRecurring;

    @Field(type = FieldType.Text)
    private String recurrenceRule;

    @Field(type = FieldType.Text)
    private String location;

    @Field(type = FieldType.Boolean)
    private Boolean isLunarCalendar;

    @Field(type = FieldType.Integer)
    private Integer reminderDays;

    @Field(type = FieldType.Long)
    private Long createdByUserId;

    @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime updatedAt;

    public static EventSearchDocument fromEvent(Event event) {
        return EventSearchDocument.builder()
                .id(event.getId())
                .familyTreeId(event.getFamilyTreeId())
                .personId(event.getPersonId())
                .eventTypeId(event.getEventTypeId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .eventTime(event.getEventTime() != null ? event.getEventTime().toString() : null)
                .isRecurring(event.getIsRecurring())
                .recurrenceRule(event.getRecurrenceRule())
                .location(event.getLocation())
                .isLunarCalendar(event.getIsLunarCalendar())
                .reminderDays(event.getReminderDays())
                .createdByUserId(event.getCreatedByUserId())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

    public Event toEventEntity() {
        return Event.builder()
                .id(this.id)
                .familyTreeId(this.familyTreeId)
                .personId(this.personId)
                .eventTypeId(this.eventTypeId)
                .title(this.title)
                .description(this.description)
                .eventDate(this.eventDate)
                .eventTime(this.eventTime != null ? java.time.LocalTime.parse(this.eventTime) : null)
                .isRecurring(this.isRecurring != null ? this.isRecurring : Boolean.FALSE)
                .recurrenceRule(this.recurrenceRule)
                .location(this.location)
                .isLunarCalendar(this.isLunarCalendar != null ? this.isLunarCalendar : Boolean.FALSE)
                .reminderDays(this.reminderDays)
                .createdByUserId(this.createdByUserId)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}


