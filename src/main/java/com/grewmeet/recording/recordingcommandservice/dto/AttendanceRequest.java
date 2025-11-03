package com.grewmeet.recording.recordingcommandservice.dto;

import java.time.LocalDateTime;

public record AttendanceRequest(
        LocalDateTime attendanceTime
) {
    public static AttendanceRequest create() {
        return new AttendanceRequest(LocalDateTime.now());
    }

    public static AttendanceRequest of(LocalDateTime attendanceTime) {
        return new AttendanceRequest(attendanceTime != null ? attendanceTime : LocalDateTime.now());
    }
}