package com.grewmeet.recording.recordingcommandservice.domain.outbox;

import com.grewmeet.recording.recordingcommandservice.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String eventType;

    @Column(nullable = false, length = 100)
    private String aggregateType;

    @Column(nullable = false)
    private Long aggregateId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status = OutboxStatus.PENDING;

    private LocalDateTime processedAt;

    private Outbox(String eventType, String aggregateType, Long aggregateId, String payload) {
        this.eventType = eventType;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.payload = payload;
    }

    public static Outbox create(String eventType, String aggregateType, Long aggregateId, String payload) {
        return new Outbox(eventType, aggregateType, aggregateId, payload);
    }

    public void markAsProcessed() {
        this.status = OutboxStatus.PROCESSED;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = OutboxStatus.FAILED;
    }
}