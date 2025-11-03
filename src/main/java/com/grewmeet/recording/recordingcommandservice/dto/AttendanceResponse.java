package com.grewmeet.recording.recordingcommandservice.dto;

import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceRecord;

import java.time.LocalDateTime;

public record AttendanceResponse(
        Long id,
        String userId,
        LocalDateTime attendanceTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AttendanceResponse from(AttendanceRecord record) {
        return new AttendanceResponse(
                record.getId(),
                record.getUserId(),
                record.getAttendanceTime(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }
}