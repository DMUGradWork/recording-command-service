package com.grewmeet.recording.recordingcommandservice.event.schema.outgoing;

import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceRecord;
import java.time.LocalDateTime;

public record AttendanceRecordCreated(
    Long id,                        // 출석 기록 ID
    String userId,                  // 사용자 ID
    LocalDateTime attendanceTime,   // 출석 시간
    LocalDateTime createdAt         // 기록 생성 시간
) {
    public static AttendanceRecordCreated from(AttendanceRecord record) {
        return new AttendanceRecordCreated(
            record.getId(),
            record.getUserId(),
            record.getAttendanceTime(),
            record.getCreatedAt()
        );
    }
}