package com.grewmeet.recording.recordingcommandservice.repository;

import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceRecord;
import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    
    Optional<AttendanceRecord> findByUserIdAndSessionIdAndStatus(String userId, String sessionId, AttendanceStatus status);
    
    Optional<AttendanceRecord> findByIdAndUserId(Long id, String userId);
}