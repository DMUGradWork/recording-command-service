package com.grewmeet.recording.recordingcommandservice.dto;

import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateAttendanceRequest(
        @NotNull(message = "출석 기록 ID는 필수입니다")
        Long recordId,
        
        LocalDateTime attendanceTime,
        
        AttendanceStatus status
) {
}
