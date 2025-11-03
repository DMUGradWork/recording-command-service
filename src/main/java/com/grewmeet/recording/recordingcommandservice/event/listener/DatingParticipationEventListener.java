package com.grewmeet.recording.recordingcommandservice.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grewmeet.recording.recordingcommandservice.domain.dating.DatingParticipation;
import com.grewmeet.recording.recordingcommandservice.event.schema.incoming.DatingParticipationReceived;
import com.grewmeet.recording.recordingcommandservice.event.schema.outgoing.DatingParticipationRecorded;
import com.grewmeet.recording.recordingcommandservice.repository.DatingParticipationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DatingParticipationEventListener {

    private static final String TOPIC = "grewmeet.recording.dating-participation";
    
    private final DatingParticipationRepository datingParticipationRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "grewmeet.dating.participation.completed", groupId = "recording-command-service")
    public void handleDatingParticipationCompleted(String eventJson) {
        try {
            log.info("데이팅 참여 완료 이벤트 수신: {}", eventJson);
            
            DatingParticipationReceived receivedEvent = objectMapper.readValue(eventJson, DatingParticipationReceived.class);
            
            // 중복 체크
            if (datingParticipationRepository.existsByMeetingIdAndUserId(
                    receivedEvent.meetingId(), receivedEvent.userId())) {
                log.warn("이미 기록된 데이팅 참여: meetingId={}, userId={}", 
                        receivedEvent.meetingId(), receivedEvent.userId());
                return;
            }
            
            // 참여 기록 저장
            DatingParticipation participation = DatingParticipation.create(
                    receivedEvent.studyGroupId(),
                    receivedEvent.userId(),
                    receivedEvent.meetingId(),
                    receivedEvent.studyMeetingEventName(),
                    receivedEvent.when(),
                    receivedEvent.createdAt()
            );
            
            DatingParticipation saved = datingParticipationRepository.save(participation);
            log.info("데이팅 참여 기록 저장 완료: id={}, userId={}", saved.getId(), saved.getUserId());
            
            // Query Service로 이벤트 발행
            DatingParticipationRecorded recordedEvent = DatingParticipationRecorded.from(saved);
            String payload = objectMapper.writeValueAsString(recordedEvent);
            String key = "DatingParticipation:" + saved.getMeetingId();
            
            kafkaTemplate.send(TOPIC, key, payload)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("데이팅 참여 기록 이벤트 발행 완료: id={}", saved.getId());
                        } else {
                            log.error("데이팅 참여 기록 이벤트 발행 실패", ex);
                        }
                    });
            
        } catch (JsonProcessingException e) {
            log.error("데이팅 참여 이벤트 파싱 실패: {}", eventJson, e);
        } catch (Exception e) {
            log.error("데이팅 참여 이벤트 처리 실패: {}", eventJson, e);
        }
    }
}