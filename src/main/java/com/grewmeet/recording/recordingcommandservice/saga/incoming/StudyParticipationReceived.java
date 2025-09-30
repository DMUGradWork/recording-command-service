package com.grewmeet.recording.recordingcommandservice.saga.incoming;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 스터디 서비스로부터 받는 참여 완료 이벤트
 * StudyMeetingParticipationCompleted
 */
public record StudyParticipationReceived(
    UUID studyGroupId,       // 스터디 그룹 ID
    UUID meetingId,          // 미팅 ID
    UUID userId,             // 사용자 ID
    LocalDateTime completedAt, // 완료 시점
    String studyGroupName,   // 스터디 그룹명
    String meetingName       // 미팅명
) {}