package com.grewmeet.recording.recordingcommandservice.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record AttendanceRequest(
        @NotBlank(message = "세션 ID는 필수입니다")
        String sessionId,
        
        LocalDateTime attendanceTime
) {
    public static AttendanceRequest create(String sessionId) {
        return new AttendanceRequest(sessionId, LocalDateTime.now());
    }
    
    public static AttendanceRequest of(String sessionId, LocalDateTime attendanceTime) {
        return new AttendanceRequest(sessionId, attendanceTime != null ? attendanceTime : LocalDateTime.now());
    }
}