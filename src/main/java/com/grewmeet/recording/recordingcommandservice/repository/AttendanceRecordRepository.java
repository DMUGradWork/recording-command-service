package com.grewmeet.recording.recordingcommandservice.repository;

import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    Optional<AttendanceRecord> findByIdAndUserId(Long id, String userId);

    /**
     * 특정 사용자의 날짜 범위 내 출석 기록 조회 (같은 날 중복 체크용)
     */
    Optional<AttendanceRecord> findByUserIdAndAttendanceTimeBetween(
            String userId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );
}