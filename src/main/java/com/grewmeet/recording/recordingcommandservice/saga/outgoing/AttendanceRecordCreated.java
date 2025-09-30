package com.grewmeet.recording.recordingcommandservice.saga.outgoing;

import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceRecord;
import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceStatus;

import java.time.LocalDateTime;

public record AttendanceRecordCreated(
    Long id,                        // 출석 기록 ID
    String userId,                  // 사용자 ID
    String sessionId,               // 세션 ID
    LocalDateTime attendanceTime,   // 출석 시간
    AttendanceStatus status,        // 출석 상태
    LocalDateTime createdAt         // 기록 생성 시간
) {
    public static AttendanceRecordCreated from(AttendanceRecord record) {
        return new AttendanceRecordCreated(
            record.getId(),
            record.getUserId(),
            record.getSessionId(),
            record.getAttendanceTime(),
            record.getStatus(),
            record.getCreatedAt()
        );
    }
}