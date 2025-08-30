package com.grewmeet.recording.recordingcommandservice.event.publisher;

import com.grewmeet.recording.recordingcommandservice.domain.outbox.Outbox;
import com.grewmeet.recording.recordingcommandservice.domain.outbox.OutboxStatus;
import com.grewmeet.recording.recordingcommandservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisherImpl implements OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    @Scheduled(fixedDelay = 5000) // 5초마다 실행
    public void publishPendingEvents() {
        List<Outbox> pendingEvents = outboxRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        
        if (pendingEvents.isEmpty()) {
            return;
        }
        
        log.info("처리할 Outbox 이벤트 수: {}", pendingEvents.size());
        
        for (Outbox outboxEvent : pendingEvents) {
            try {
                String topic = generateTopicName(outboxEvent.getEventType());
                String key = generateKey(outboxEvent);
                
                kafkaTemplate.send(topic, key, outboxEvent.getPayload())
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                markAsProcessed(outboxEvent.getId());
                                log.info("Outbox 이벤트 발행 성공: id={}, eventType={}, topic={}", 
                                        outboxEvent.getId(), outboxEvent.getEventType(), topic);
                            } else {
                                markAsFailed(outboxEvent.getId());
                                log.error("Outbox 이벤트 발행 실패: id={}, eventType={}, topic={}", 
                                        outboxEvent.getId(), outboxEvent.getEventType(), topic, ex);
                            }
                        });
                        
            } catch (Exception e) {
                markAsFailed(outboxEvent.getId());
                log.error("Outbox 이벤트 처리 중 예외 발생: id={}, eventType={}", 
                        outboxEvent.getId(), outboxEvent.getEventType(), e);
            }
        }
    }

    private String generateTopicName(String eventType) {
        return switch (eventType) {
            case "AttendanceRecorded" -> "grewmeet.recording.attendance";
            case "DatingParticipationRecorded" -> "grewmeet.recording.dating-participation";
            case "StudyGroupParticipationRecorded" -> "grewmeet.recording.study-participation";
            default -> "grewmeet.recording.unknown";
        };
    }

    private String generateKey(Outbox outboxEvent) {
        return outboxEvent.getAggregateType() + ":" + outboxEvent.getAggregateId();
    }

    private void markAsProcessed(Long outboxId) {
        try {
            outboxRepository.findById(outboxId)
                    .ifPresent(outbox -> {
                        outbox.markAsProcessed();
                        outboxRepository.save(outbox);
                    });
        } catch (Exception e) {
            log.error("Outbox 상태 업데이트 실패: id={}", outboxId, e);
        }
    }

    private void markAsFailed(Long outboxId) {
        try {
            outboxRepository.findById(outboxId)
                    .ifPresent(outbox -> {
                        outbox.markAsFailed();
                        outboxRepository.save(outbox);
                    });
        } catch (Exception e) {
            log.error("Outbox 실패 상태 업데이트 실패: id={}", outboxId, e);
        }
    }
}