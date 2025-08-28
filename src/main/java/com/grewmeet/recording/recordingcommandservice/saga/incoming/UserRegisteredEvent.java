package com.grewmeet.recording.recordingcommandservice.saga.incoming;

import java.time.LocalDateTime;

public record UserRegisteredEvent(
        String email,           // 사용자 이메일
        String userId,          // 사용자 ID
        LocalDateTime registeredAt  // 등록 시점
) {}