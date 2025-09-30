package com.grewmeet.recording.recordingcommandservice.saga.outgoing;

import com.grewmeet.recording.recordingcommandservice.domain.study.StudyParticipation;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Query Service로 발행할 스터디 참여 기록 이벤트
 */
public record StudyParticipationRecorded(
    Long id,                    // 참여 기록 ID
    UUID studyGroupId,          // 스터디 그룹 ID
    UUID userId,                // 사용자 ID
    UUID meetingId,             // 미팅 ID
    String studyGroupName,      // 스터디 그룹명
    String meetingName,         // 미팅명
    LocalDateTime completedAt,  // 완료 시간
    LocalDateTime recordedAt    // 기록 생성 시간
) {
    public static StudyParticipationRecorded from(StudyParticipation participation) {
        return new StudyParticipationRecorded(
                participation.getId(),
                participation.getStudyGroupId(),
                participation.getUserId(),
                participation.getMeetingId(),
                participation.getStudyGroupName(),
                participation.getMeetingName(),
                participation.getCompletedAt(),
                participation.getCreatedAt()
        );
    }
}