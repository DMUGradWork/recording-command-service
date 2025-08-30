package com.grewmeet.recording.recordingcommandservice.saga.outgoing;

import com.grewmeet.recording.recordingcommandservice.saga.incoming.StudyParticipationReceived;

import java.time.LocalDateTime;

public record StudyParticipationRecorded(
    Long id,                            // 참여 기록 ID
    String userId,                      // 사용자 ID
    String studyGroupId,                // 스터디 그룹 ID
    String sessionId,                   // 세션 ID
    LocalDateTime participationDate,    // 참여 날짜
    String status,                      // 참여 상태
    LocalDateTime recordedAt            // 기록 생성 시간
) {
    public static StudyParticipationRecorded from(StudyParticipationReceived received) {
        return new StudyParticipationRecorded(
                null,  // id는 Query Service에서 생성
                received.userId().toString(),
                received.studyMeetingId().toString(),
                received.participantId().toString(),  // sessionId로 사용
                received.completedAt(),
                "ATTENDED",
                LocalDateTime.now()
        );
    }
}