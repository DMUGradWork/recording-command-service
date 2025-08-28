package com.grewmeet.recording.recordingcommandservice.saga.incoming;

import java.time.LocalDateTime;

public record DatingParticipationReceived(
    Long datingMeetingId,    // 데이팅 이벤트 ID
    Long participantId,      // 참가자 ID  
    Long userId,             // 사용자 ID
    LocalDateTime joinedAt   // 참여 시점
) {}