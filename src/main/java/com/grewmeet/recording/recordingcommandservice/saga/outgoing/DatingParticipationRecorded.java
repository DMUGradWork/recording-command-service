package com.grewmeet.recording.recordingcommandservice.saga.outgoing;

import com.grewmeet.recording.recordingcommandservice.domain.dating.DatingParticipation;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Query Service로 발행할 데이팅 참여 기록 이벤트
 */
public record DatingParticipationRecorded(
    Long id,                        // 참여 기록 ID
    UUID datingGroupId,             // 데이팅 그룹 ID
    UUID userId,                    // 사용자 ID
    UUID meetingId,                 // 미팅 ID
    String eventName,               // 이벤트명
    LocalDateTime scheduledAt,      // 예정 시간
    LocalDateTime meetingCreatedAt, // 미팅 생성 시간
    LocalDateTime recordedAt        // 기록 생성 시간
) {
    public static DatingParticipationRecorded from(DatingParticipation participation) {
        return new DatingParticipationRecorded(
                participation.getId(),
                participation.getDatingGroupId(),
                participation.getUserId(),
                participation.getMeetingId(),
                participation.getEventName(),
                participation.getScheduledAt(),
                participation.getMeetingCreatedAt(),
                participation.getCreatedAt()
        );
    }
}