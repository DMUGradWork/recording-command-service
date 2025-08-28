package com.grewmeet.recording.recordingcommandservice.service;

import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceRecord;
import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceStatus;
import com.grewmeet.recording.recordingcommandservice.domain.user.User;
import com.grewmeet.recording.recordingcommandservice.dto.AttendanceRequest;
import com.grewmeet.recording.recordingcommandservice.dto.AttendanceResponse;
import com.grewmeet.recording.recordingcommandservice.saga.outgoing.AttendanceRecordCreated;
import com.grewmeet.recording.recordingcommandservice.repository.AttendanceRecordRepository;
import com.grewmeet.recording.recordingcommandservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final UserRepository userRepository;
    private final OutboxService outboxService;

    @Override
    public AttendanceResponse recordAttendance(AttendanceRequest request) {
        log.info("출석 기록 처리 시작: userId={}, sessionId={}", request.userId(), request.sessionId());
        
        // 사용자 존재 여부 및 출석 가능 여부 검증
        User user = userRepository.findByUserId(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + request.userId()));
        
        if (!user.isValidForAttendance()) {
            throw new IllegalStateException("출석 처리가 불가능한 사용자입니다: " + request.userId());
        }
        
        // 중복 출석 검증
        AttendanceRecord existingRecord = attendanceRecordRepository
                .findByUserIdAndSessionIdAndStatus(request.userId(), request.sessionId(), AttendanceStatus.PRESENT)
                .orElse(null);
        
        if (existingRecord != null) {
            throw new IllegalStateException("이미 출석 처리된 사용자입니다.");
        }
        
        AttendanceRecord record = AttendanceRecord.create(
                request.userId(),
                request.sessionId(),
                request.attendanceTime()
        );
        
        AttendanceRecord savedRecord = attendanceRecordRepository.save(record);
        log.info("출석 기록 완료: id={}", savedRecord.getId());
        
        AttendanceRecordCreated eventData = AttendanceRecordCreated.from(savedRecord);
        outboxService.publishEvent("AttendanceRecorded", "AttendanceRecord", savedRecord.getId(), eventData);
        
        return AttendanceResponse.from(savedRecord);
    }
}