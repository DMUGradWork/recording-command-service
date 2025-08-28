package com.grewmeet.recording.recordingcommandservice.domain.outbox;

public enum OutboxStatus {
    PENDING,
    PROCESSED,
    FAILED
}