package com.grewmeet.recording.recordingcommandservice.saga.outgoing;

import com.grewmeet.recording.recordingcommandservice.saga.incoming.DatingParticipationReceived;

import java.time.LocalDateTime;

public record DatingParticipationRecorded(
    Long id,                            // 참여 기록 ID
    String userId,                      // 사용자 ID
    String datingEventId,               // 데이팅 이벤트 ID
    LocalDateTime participationDate,    // 참여 날짜
    String status,                      // 참여 상태
    LocalDateTime recordedAt            // 기록 생성 시간
) {
    public static DatingParticipationRecorded from(DatingParticipationReceived received) {
        return new DatingParticipationRecorded(
                null,  // id는 Query Service에서 생성
                received.userId().toString(),
                received.datingMeetingId().toString(),
                received.joinedAt(),
                "PARTICIPATED",
                LocalDateTime.now()
        );
    }
}