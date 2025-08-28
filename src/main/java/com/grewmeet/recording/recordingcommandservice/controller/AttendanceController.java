package com.grewmeet.recording.recordingcommandservice.controller;

import com.grewmeet.recording.recordingcommandservice.dto.AttendanceRequest;
import com.grewmeet.recording.recordingcommandservice.dto.AttendanceResponse;
import com.grewmeet.recording.recordingcommandservice.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<AttendanceResponse> recordAttendance(@Valid @RequestBody AttendanceRequest request) {
        log.info("출석 기록 요청: userId={}, sessionId={}", request.userId(), request.sessionId());
        
        AttendanceResponse response = attendanceService.recordAttendance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}