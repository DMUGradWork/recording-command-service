package com.grewmeet.recording.recordingcommandservice.service;

import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceRecord;
import com.grewmeet.recording.recordingcommandservice.dto.AttendanceRequest;
import com.grewmeet.recording.recordingcommandservice.dto.AttendanceResponse;
import com.grewmeet.recording.recordingcommandservice.dto.DeleteAttendanceRequest;
import com.grewmeet.recording.recordingcommandservice.dto.UpdateAttendanceRequest;
import com.grewmeet.recording.recordingcommandservice.event.publisher.RecordingEventPublisher;
import com.grewmeet.recording.recordingcommandservice.repository.AttendanceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final RecordingEventPublisher recordingEventPublisher;

    @Override
    public AttendanceResponse createAttendance(String userId, AttendanceRequest request) {
        log.info("출석 기록 생성 시작: userId={}", userId);

        // 같은 날 중복 출석 체크
        LocalDateTime attendanceTime = request.attendanceTime() != null ? request.attendanceTime() : LocalDateTime.now();
        LocalDate attendanceDate = attendanceTime.toLocalDate();
        LocalDateTime startOfDay = attendanceDate.atStartOfDay();
        LocalDateTime endOfDay = attendanceDate.atTime(23, 59, 59);

        attendanceRecordRepository
                .findByUserIdAndAttendanceTimeBetween(userId, startOfDay, endOfDay)
                .ifPresent(record -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "오늘 이미 출석 처리되었습니다. 기존 출석 시간: " + record.getAttendanceTime());
                });

        AttendanceRecord record = AttendanceRecord.create(userId, attendanceTime);

        AttendanceRecord saved = attendanceRecordRepository.save(record);

        log.info("이벤트 발행 직전: publisher={}", recordingEventPublisher.getClass().getName());
        recordingEventPublisher.publishAttendanceCreated(saved);
        log.info("이벤트 발행 직후");

        log.info("출석 기록 생성 완료: id={}", saved.getId());
        return AttendanceResponse.from(saved);
    }

    @Override
    public void updateAttendance(String userId, UpdateAttendanceRequest request) {
        log.info("출석 기록 수정 시작: recordId={}, userId={}", request.recordId(), userId);

        AttendanceRecord record = attendanceRecordRepository
                .findByIdAndUserId(request.recordId(), userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "출석 기록을 찾을 수 없습니다."));

        record.update(request.attendanceTime());
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