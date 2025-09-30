package com.grewmeet.recording.recordingcommandservice.event.publisher;

import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceRecord;

/**
 * 기록 서비스의 도메인 이벤트를 발행하는 Publisher 인터페이스
 */
public interface RecordingEventPublisher {
    
    /**
     * 출석 기록 생성 이벤트 발행
     */
    void publishAttendanceCreated(AttendanceRecord record);
    
    /**
     * 출석 기록 수정 이벤트 발행
     */
    void publishAttendanceUpdated(AttendanceRecord record);
    
    /**
     * 출석 기록 삭제 이벤트 발행
     */
    void publishAttendanceDeleted(Long recordId, String userId);
}
