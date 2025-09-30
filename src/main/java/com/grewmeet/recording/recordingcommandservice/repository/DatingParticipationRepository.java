package com.grewmeet.recording.recordingcommandservice.repository;

import com.grewmeet.recording.recordingcommandservice.domain.dating.DatingParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DatingParticipationRepository extends JpaRepository<DatingParticipation, Long> {
    
    boolean existsByMeetingIdAndUserId(UUID meetingId, UUID userId);
}
