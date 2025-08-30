package com.grewmeet.recording.recordingcommandservice.event.publisher;

public interface OutboxPublisher {
    
    /**
     * 대기 중인 Outbox 이벤트들을 Kafka로 발행합니다.
     */
    void publishPendingEvents();
}