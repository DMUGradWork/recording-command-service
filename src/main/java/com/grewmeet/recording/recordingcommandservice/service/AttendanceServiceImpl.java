package com.grewmeet.recording.recordingcommandservice.service;

import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceRecord;
import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceStatus;
import com.grewmeet.recording.recordingcommandservice.domain.user.User;
import com.grewmeet.recording.recordingcommandservice.dto.AttendanceRequest;
import com.grewmeet.recording.recordingcommandservice.dto.AttendanceResponse;
import com.grewmeet.recording.recordingcommandservice.dto.DeleteAttendanceRequest;
import com.grewmeet.recording.recordingcommandservice.dto.UpdateAttendanceRequest;
import com.grewmeet.recording.recordingcommandservice.event.publisher.RecordingEventPublisher;
import com.grewmeet.recording.recordingcommandservice.repository.AttendanceRecordRepository;
import com.grewmeet.recording.recordingcommandservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final UserRepository userRepository;
    private final RecordingEventPublisher recordingEventPublisher;

    @Override
    public AttendanceResponse createAttendance(String userId, AttendanceRequest request) {
        log.info("출석 기록 생성 시작: userId={}, sessionId={}", userId, request.sessionId());
        
        // 사용자 존재 여부 및 출석 가능 여부 검증
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다: " + userId));
        
        if (!user.isValidForAttendance()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "출석 처리가 불가능한 사용자입니다: " + userId);
        }
        
        // 중복 출석 검증
        attendanceRecordRepository
                .findByUserIdAndSessionIdAndStatus(userId, request.sessionId(), AttendanceStatus.PRESENT)
                .ifPresent(record -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 출석 처리된 사용자입니다.");
                });
        
        AttendanceRecord record = AttendanceRecord.create(
                userId,
                request.sessionId(),
                request.attendanceTime()
        );
        
        AttendanceRecord saved = attendanceRecordRepository.save(record);
        recordingEventPublisher.publishAttendanceCreated(saved);
        
        log.info("출석 기록 생성 완료: id={}", saved.getId());
        return AttendanceResponse.from(saved);
    }

    @Override
    public void updateAttendance(String userId, UpdateAttendanceRequest request) {
        log.info("출석 기록 수정 시작: recordId={}, userId={}", request.recordId(), userId);
        
        AttendanceRecord record = attendanceRecordRepository
                .findByIdAndUserId(request.recordId(), userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "출석 기록을 찾을 수 없습니다."));
        
        record.update(request.attendanceTime(), request.status());
        AttendanceRecord saved = attendanceRecordRepository.save(record);
        recordingEventPublisher.publishAttendanceUpdated(saved);
        
        log.info("출석 기록 수정 완료: id={}", saved.getId());
    }

    @Override
    public void deleteAttendance(String userId, DeleteAttendanceRequest request) {
        log.info("출석 기록 삭제 시작: recordId={}, userId={}", request.recordId(), userId);
        
        AttendanceRecord record = attendanceRecordRepository
                .findByIdAndUserId(request.recordId(), userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "출석 기록을 찾을 수 없습니다."));
        
        attendanceRecordRepository.delete(record);
        recordingEventPublisher.publishAttendanceDeleted(record.getId(), userId);
        
        log.info("출석 기록 삭제 완료: id={}", record.getId());
    }
}