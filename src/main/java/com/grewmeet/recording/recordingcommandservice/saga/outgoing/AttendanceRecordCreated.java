package com.grewmeet.recording.recordingcommandservice.saga.outgoing;

import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceRecordCreated(
    Long id,                        // 출석 기록 ID
    String userId,                  // 사용자 ID
    LocalDate attendanceDate,       // 출석 날짜
    Boolean isAttended,             // 출석 여부
    LocalDateTime checkInTime,      // 체크인 시간
    LocalDateTime recordedAt        // 기록 생성 시간
) {
    public static AttendanceRecordCreated from(AttendanceRecord record) {
        return new AttendanceRecordCreated(
            record.getId(),
            record.getUserId(),
            record.getAttendanceDate(),
            record.getIsAttended(),
            record.getCheckInTime(),
            record.getRecordedAt()
        );
    }
}