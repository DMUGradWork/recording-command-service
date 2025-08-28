package com.grewmeet.recording.recordingcommandservice.service;

import com.grewmeet.recording.recordingcommandservice.dto.AttendanceRequest;
import com.grewmeet.recording.recordingcommandservice.dto.AttendanceResponse;

public interface AttendanceService {
    
    AttendanceResponse recordAttendance(AttendanceRequest request);
}