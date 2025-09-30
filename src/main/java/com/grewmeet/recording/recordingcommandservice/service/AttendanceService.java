package com.grewmeet.recording.recordingcommandservice.service;

import com.grewmeet.recording.recordingcommandservice.dto.AttendanceRequest;
import com.grewmeet.recording.recordingcommandservice.dto.AttendanceResponse;
import com.grewmeet.recording.recordingcommandservice.dto.DeleteAttendanceRequest;
import com.grewmeet.recording.recordingcommandservice.dto.UpdateAttendanceRequest;

public interface AttendanceService {
    
    /**
     * 출석 기록 생성
     */
    AttendanceResponse createAttendance(String userId, AttendanceRequest request);
    
    /**
     * 출석 기록 수정
     */
    void updateAttendance(String userId, UpdateAttendanceRequest request);
    
    /**
     * 출석 기록 삭제
     */
    void deleteAttendance(String userId, DeleteAttendanceRequest request);
}