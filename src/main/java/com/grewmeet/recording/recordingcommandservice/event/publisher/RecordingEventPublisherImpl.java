package com.grewmeet.recording.recordingcommandservice.event.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grewmeet.recording.recordingcommandservice.domain.attendance.AttendanceRecord;
import com.grewmeet.recording.recordingcommandservice.saga.outgoing.AttendanceRecordCreated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 기록 서비스의 도메인 이벤트를 Kafka로 직접 발행하는 구현체
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RecordingEventPublisherImpl implements RecordingEventPublisher {
    
    private static final String TOPIC_ATTENDANCE = "grewmeet.recording.attendance";
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    @Override
    public void publishAttendanceCreated(AttendanceRecord record) {
        log.info("출석 기록 생성 이벤트 발행: recordId={}, userId={}", record.getId(), record.getUserId());
        AttendanceRecordCreated eventData = AttendanceRecordCreated.from(record);
        publishEvent("AttendanceRecordCreated", eventData);
    }
    
    @Override
    public void publishAttendanceUpdated(AttendanceRecord record) {
        log.info("출석 기록 수정 이벤트 발행: recordId={}, userId={}", record.getId(), record.getUserId());
        AttendanceRecordCreated eventData = AttendanceRecordCreated.from(record);
        publishEvent("AttendanceRecordUpdated", eventData);
    }
    
    @Override
    public void publishAttendanceDeleted(Long recordId, String userId) {
        log.info("출석 기록 삭제 이벤트 발행: recordId={}, userId={}", recordId, userId);
        publishEvent("AttendanceRecordDeleted", new AttendanceRecordDeleted(recordId, userId));
    }
    
    private void publishEvent(String eventType, Object eventData) {
        try {
            String payload = objectMapper.writeValueAsString(eventData);
            String key = eventType + ":" + System.currentTimeMillis();
            
            kafkaTemplate.send(TOPIC_ATTENDANCE, key, payload)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("이벤트 발행 성공: eventType={}, topic={}", eventType, TOPIC_ATTENDANCE);
                        } else {
                            log.error("이벤트 발행 실패: eventType={}, topic={}", eventType, TOPIC_ATTENDANCE, ex);
                        }
                    });
        } catch (Exception e) {
            log.error("이벤트 발행 중 예외 발생: eventType={}", eventType, e);
        }
    }
    
    /**
     * 출석 기록 삭제 이벤트 데이터
     */
    private record AttendanceRecordDeleted(Long id, String userId) {}
}
