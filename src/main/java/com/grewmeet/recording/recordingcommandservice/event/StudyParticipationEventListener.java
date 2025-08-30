package com.grewmeet.recording.recordingcommandservice.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grewmeet.recording.recordingcommandservice.saga.incoming.StudyParticipationReceived;
import com.grewmeet.recording.recordingcommandservice.saga.outgoing.StudyParticipationRecorded;
import com.grewmeet.recording.recordingcommandservice.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudyParticipationEventListener {

    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "grewmeet.study.participation.received", groupId = "recording-command-service")
    public void handleStudyParticipationReceived(String eventJson) {
        try {
            log.info("스터디 참여 이벤트 수신: {}", eventJson);
            
            StudyParticipationReceived receivedEvent = objectMapper.readValue(eventJson, StudyParticipationReceived.class);
            
            // 수신 이벤트를 기록용 이벤트로 변환
            StudyParticipationRecorded recordedEvent = StudyParticipationRecorded.from(receivedEvent);
            
            // Outbox를 통해 Query Service로 이벤트 발행
            outboxService.publishEvent(
                "StudyParticipationRecorded", 
                "StudyParticipation", 
                receivedEvent.participantId(), 
                recordedEvent
            );
            
            log.info("스터디 참여 기록 이벤트 발행 완료: participantId={}, userId={}", 
                    receivedEvent.participantId(), receivedEvent.userId());
            
        } catch (JsonProcessingException e) {
            log.error("스터디 참여 이벤트 파싱 실패: {}", eventJson, e);
        } catch (Exception e) {
            log.error("스터디 참여 이벤트 처리 실패: {}", eventJson, e);
        }
    }
}