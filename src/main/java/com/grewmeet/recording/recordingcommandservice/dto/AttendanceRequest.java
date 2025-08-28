package com.grewmeet.recording.recordingcommandservice.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record AttendanceRequest(
        @NotBlank(message = "사용자 ID는 필수입니다")
        String userId,
        
        @NotBlank(message = "세션 ID는 필수입니다")
        String sessionId,
        
        LocalDateTime attendanceTime
) {
    public static AttendanceRequest create(String userId, String sessionId) {
        return new AttendanceRequest(userId, sessionId, LocalDateTime.now());
    }
    
    public static AttendanceRequest of(String userId, String sessionId, LocalDateTime attendanceTime) {
        return new AttendanceRequest(userId, sessionId, attendanceTime != null ? attendanceTime : LocalDateTime.now());
    }
}