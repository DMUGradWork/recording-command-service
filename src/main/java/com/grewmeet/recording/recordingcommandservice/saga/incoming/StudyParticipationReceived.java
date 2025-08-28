package com.grewmeet.recording.recordingcommandservice.saga.incoming;

import java.time.LocalDateTime;

public record StudyParticipationReceived(
    Long studyMeetingId,     // 스터디 미팅 ID
    Long participantId,      // 참가자 ID
    Long userId,             // 사용자 ID  
    LocalDateTime completedAt // 완료 시점
) {}