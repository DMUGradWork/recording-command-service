package com.grewmeet.recording.recordingcommandservice.domain.study;

import com.grewmeet.recording.recordingcommandservice.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "study_participations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyParticipation extends BaseEntity {

    @Column(nullable = false)
    private UUID studyGroupId;
    
    @Column(nullable = false)
    private UUID userId;
    
    @Column(nullable = false)
    private UUID meetingId;
    
    @Column(nullable = false)
    private String studyGroupName;
    
    @Column(nullable = false)
    private String meetingName;
    
    @Column(nullable = false)
    private LocalDateTime completedAt;
    
    public static StudyParticipation create(
            UUID studyGroupId,
            UUID userId,
            UUID meetingId,
            String studyGroupName,
            String meetingName,
            LocalDateTime completedAt) {
        StudyParticipation participation = new StudyParticipation();
        participation.studyGroupId = studyGroupId;
        participation.userId = userId;
        participation.meetingId = meetingId;
        participation.studyGroupName = studyGroupName;
        participation.meetingName = meetingName;
        participation.completedAt = completedAt;
        return participation;
    }
}
