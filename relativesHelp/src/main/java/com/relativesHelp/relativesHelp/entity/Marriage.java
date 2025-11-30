package com.relativesHelp.relativesHelp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "marriages")
@Getter
@Setter
@NoArgsConstructor
public class Marriage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @Column(name = "marriage_date")
    private LocalDate marriageDate;

    @Column(name = "marriage_date_lunar")
    private LocalDate marriageDateLunar;

    @Column(name = "divorce_date")
    private LocalDate divorceDate;

    @Enumerated(EnumType.STRING)
    private MarriageStatus status = MarriageStatus.MARRIED;

    @Column(name = "marriage_location", length = 500)
    private String marriageLocation;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum MarriageStatus { MARRIED, DIVORCED, WIDOWED }
}