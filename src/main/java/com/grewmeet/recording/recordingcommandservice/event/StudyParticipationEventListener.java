package com.grewmeet.recording.recordingcommandservice.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grewmeet.recording.recordingcommandservice.domain.study.StudyParticipation;
import com.grewmeet.recording.recordingcommandservice.repository.StudyParticipationRepository;
import com.grewmeet.recording.recordingcommandservice.saga.incoming.StudyParticipationReceived;
import com.grewmeet.recording.recordingcommandservice.saga.outgoing.StudyParticipationRecorded;
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
public class StudyParticipationEventListener {

    private static final String TOPIC = "grewmeet.recording.study-participation";
    
    private final StudyParticipationRepository studyParticipationRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "grewmeet.study.participation.completed", groupId = "recording-command-service")
    public void handleStudyParticipationCompleted(String eventJson) {
        try {
            log.info("스터디 참여 완료 이벤트 수신: {}", eventJson);
            
            StudyParticipationReceived receivedEvent = objectMapper.readValue(eventJson, StudyParticipationReceived.class);
            
            // 중복 체크
            if (studyParticipationRepository.existsByMeetingIdAndUserId(
                    receivedEvent.meetingId(), receivedEvent.userId())) {
                log.warn("이미 기록된 스터디 참여: meetingId={}, userId={}", 
                        receivedEvent.meetingId(), receivedEvent.userId());
                return;
            }
            
            // 참여 기록 저장
            StudyParticipation participation = StudyParticipation.create(
                    receivedEvent.studyGroupId(),
                    receivedEvent.userId(),
                    receivedEvent.meetingId(),
                    receivedEvent.studyGroupName(),
                    receivedEvent.meetingName(),
                    receivedEvent.completedAt()
            );
            
            StudyParticipation saved = studyParticipationRepository.save(participation);
            log.info("스터디 참여 기록 저장 완료: id={}, userId={}", saved.getId(), saved.getUserId());
            
            // Query Service로 이벤트 발행
            StudyParticipationRecorded recordedEvent = StudyParticipationRecorded.from(saved);
            String payload = objectMapper.writeValueAsString(recordedEvent);
            String key = "StudyParticipation:" + saved.getMeetingId();
            
            kafkaTemplate.send(TOPIC, key, payload)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("스터디 참여 기록 이벤트 발행 완료: id={}", saved.getId());
                        } else {
                            log.error("스터디 참여 기록 이벤트 발행 실패", ex);
                        }
                    });
            
        } catch (JsonProcessingException e) {
            log.error("스터디 참여 이벤트 파싱 실패: {}", eventJson, e);
        } catch (Exception e) {
            log.error("스터디 참여 이벤트 처리 실패: {}", eventJson, e);
        }
    }
}