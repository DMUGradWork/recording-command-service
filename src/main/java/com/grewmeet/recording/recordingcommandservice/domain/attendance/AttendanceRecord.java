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
    private LocalDateTime attendanceTime;

    public static AttendanceRecord create(String userId, LocalDateTime attendanceTime) {
        AttendanceRecord record = new AttendanceRecord();
        record.userId = userId;
        record.attendanceTime = attendanceTime != null ? attendanceTime : LocalDateTime.now();
        return record;
    }

    /**
     * 출석 기록 수정
     */
    public void update(LocalDateTime attendanceTime) {
        if (attendanceTime != null) {
            this.attendanceTime = attendanceTime;
        }
    }
}