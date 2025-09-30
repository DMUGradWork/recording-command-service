package com.grewmeet.recording.recordingcommandservice.dto;

import jakarta.validation.constraints.NotNull;

public record DeleteAttendanceRequest(
        @NotNull(message = "출석 기록 ID는 필수입니다")
        Long recordId
) {
}
