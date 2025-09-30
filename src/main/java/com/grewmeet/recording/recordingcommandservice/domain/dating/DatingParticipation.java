package com.grewmeet.recording.recordingcommandservice.domain.dating;

import com.grewmeet.recording.recordingcommandservice.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dating_participations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DatingParticipation extends BaseEntity {

    @Column(nullable = false)
    private UUID datingGroupId;
    
    @Column(nullable = false)
    private UUID userId;
    
    @Column(nullable = false)
    private UUID meetingId;
    
    @Column(nullable = false)
    private String eventName;
    
    @Column(nullable = false)
    private LocalDateTime scheduledAt;      // 언제 모이기로 했는지 (when)
    
    @Column(nullable = false)
    private LocalDateTime meetingCreatedAt; // 방 생성 시간
    
    public static DatingParticipation create(
            UUID datingGroupId,
            UUID userId, 
            UUID meetingId,
            String eventName,
            LocalDateTime scheduledAt,
            LocalDateTime meetingCreatedAt) {
        DatingParticipation participation = new DatingParticipation();
        participation.datingGroupId = datingGroupId;
        participation.userId = userId;
        participation.meetingId = meetingId;
        participation.eventName = eventName;
        participation.scheduledAt = scheduledAt;
        participation.meetingCreatedAt = meetingCreatedAt;
        return participation;
    }
}
