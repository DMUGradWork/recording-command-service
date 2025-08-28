package com.grewmeet.recording.recordingcommandservice.repository;

import com.grewmeet.recording.recordingcommandservice.domain.outbox.Outbox;
import com.grewmeet.recording.recordingcommandservice.domain.outbox.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {
    
    List<Outbox> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}