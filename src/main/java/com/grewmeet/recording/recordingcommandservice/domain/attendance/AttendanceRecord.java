package com.grewmeet.recording.recordingcommandservice.domain.attendance;

import com.grewmeet.recording.recordingcommandservice.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttendanceRecord extends BaseEntity {

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String sessionId;

    @Column(nullable = false)
    private LocalDateTime attendanceTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    public static AttendanceRecord create(String userId, String sessionId, LocalDateTime attendanceTime) {
        AttendanceRecord record = new AttendanceRecord();
        record.userId = userId;
        record.sessionId = sessionId;
        record.attendanceTime = attendanceTime != null ? attendanceTime : LocalDateTime.now();
        record.status = AttendanceStatus.PRESENT;
        return record;
    }
    
    /**
     * 출석 기록 수정
     */
    public void update(LocalDateTime attendanceTime, AttendanceStatus status) {
        if (attendanceTime != null) {
            this.attendanceTime = attendanceTime;
        }
        if (status != null) {
            this.status = status;
        }
    }
}