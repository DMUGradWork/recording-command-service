package com.grewmeet.recording.recordingcommandservice.controller;

import com.grewmeet.recording.recordingcommandservice.dto.AttendanceRequest;
import com.grewmeet.recording.recordingcommandservice.dto.AttendanceResponse;
import com.grewmeet.recording.recordingcommandservice.dto.DeleteAttendanceRequest;
import com.grewmeet.recording.recordingcommandservice.dto.UpdateAttendanceRequest;
import com.grewmeet.recording.recordingcommandservice.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 출석 기록 Command API
 * JWT 인증을 통해 X-User-Id 헤더로 사용자 식별
 */
@RestController
@RequestMapping("/api/attendance")
@Validated
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 출석 기록 생성
     */
    @PostMapping
    public ResponseEntity<AttendanceResponse> createAttendance(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid AttendanceRequest request) {
        log.info("출석 기록 생성 요청: userId={}, sessionId={}", userId, request.sessionId());
        
        AttendanceResponse response = attendanceService.createAttendance(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/attendance/" + response.id())
                .body(response);
    }

    /**
     * 출석 기록 수정
     */
    @PatchMapping
    public ResponseEntity<Void> updateAttendance(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid UpdateAttendanceRequest request) {
        log.info("출석 기록 수정 요청: userId={}, recordId={}", userId, request.recordId());
        
        attendanceService.updateAttendance(userId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 출석 기록 삭제
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAttendance(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid DeleteAttendanceRequest request) {
        log.info("출석 기록 삭제 요청: userId={}, recordId={}", userId, request.recordId());
        
        attendanceService.deleteAttendance(userId, request);
        return ResponseEntity.noContent().build();
    }
}