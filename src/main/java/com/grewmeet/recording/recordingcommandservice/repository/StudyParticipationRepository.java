package com.grewmeet.recording.recordingcommandservice.repository;

import com.grewmeet.recording.recordingcommandservice.domain.study.StudyParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudyParticipationRepository extends JpaRepository<StudyParticipation, Long> {
    
    boolean existsByMeetingIdAndUserId(UUID meetingId, UUID userId);
}
