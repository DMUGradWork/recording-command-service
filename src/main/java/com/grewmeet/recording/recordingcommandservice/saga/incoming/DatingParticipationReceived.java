package com.grewmeet.recording.recordingcommandservice.saga.incoming;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 데이팅 서비스로부터 받는 참여 완료 이벤트
 * DatingMeetingParticipationCompleted
 */
public record DatingParticipationReceived(
    UUID studyGroupId,              // 데이팅 그룹 ID (원본 필드명 유지)
    UUID userId,                    // 어떤 회원이 참여한건지 식별
    UUID meetingId,                 // 미팅 ID
    String studyMeetingEventName,   // 데이팅 이벤트 명
    LocalDateTime when,             // 원래 언제 모이기로 했는지
    LocalDateTime createdAt         // 방이 생성된 시간
) {}