package com.relativesHelp.relativesHelp.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    // User Events
    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name("user.registered")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userProfileUpdatedTopic() {
        return TopicBuilder.name("user.profile.updated")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Family Tree Events
    @Bean
    public NewTopic familyPersonCreatedTopic() {
        return TopicBuilder.name("family.person.created")
                .partitions(5)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic familyPersonUpdatedTopic() {
        return TopicBuilder.name("family.person.updated")
                .partitions(5)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic familyRelationshipCreatedTopic() {
        return TopicBuilder.name("family.relationship.created")
                .partitions(5)
                .replicas(1)
                .build();
    }

    // Event/Calendar Events
    @Bean
    public NewTopic eventCreatedTopic() {
        return TopicBuilder.name("event.created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic eventReminderDueTopic() {
        return TopicBuilder.name("event.reminder.due")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Media Events
    @Bean
    public NewTopic mediaUploadedTopic() {
        return TopicBuilder.name("media.uploaded")
                .partitions(5)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic mediaDeletedTopic() {
        return TopicBuilder.name("media.deleted")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Notification Events
    @Bean
    public NewTopic notificationSendTopic() {
        return TopicBuilder.name("notification.send")
                .partitions(10)
                .replicas(1)
                .build();
    }
}

