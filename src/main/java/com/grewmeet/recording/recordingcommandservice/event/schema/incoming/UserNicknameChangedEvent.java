package com.grewmeet.recording.recordingcommandservice.event.schema.incoming;

import java.time.LocalDateTime;

public record UserNicknameChangedEvent(
        String userId,          // 사용자 ID
        String oldNickname,     // 기존 닉네임
        String newNickname,     // 새 닉네임
        LocalDateTime changedAt // 변경 시점
) {}