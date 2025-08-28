package com.grewmeet.recording.recordingcommandservice.dto;

import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceRecord;
import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceStatus;

import java.time.LocalDateTime;

public record AttendanceResponse(
        Long id,
        String userId,
        String sessionId,
        LocalDateTime attendanceTime,
        AttendanceStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AttendanceResponse from(AttendanceRecord record) {
        return new AttendanceResponse(
                record.getId(),
                record.getUserId(),
                record.getSessionId(),
                record.getAttendanceTime(),
                record.getStatus(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }
}