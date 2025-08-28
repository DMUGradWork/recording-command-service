package com.grewmeet.recording.recordingcommandservice.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grewmeet.recording.recordingcommandservice.domain.user.User;
import com.grewmeet.recording.recordingcommandservice.repository.UserRepository;
import com.grewmeet.recording.recordingcommandservice.saga.incoming.UserRegisteredEvent;
import com.grewmeet.recording.recordingcommandservice.saga.incoming.UserNicknameChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserEventListener {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "grewmeet.user.registered", groupId = "recording-command-service")
    public void handleUserRegistered(String eventJson) {
        try {
            log.info("사용자 등록 이벤트 수신: {}", eventJson);
            
            UserRegisteredEvent event = objectMapper.readValue(eventJson, UserRegisteredEvent.class);
            
            if (userRepository.existsByUserId(event.userId())) {
                log.warn("이미 존재하는 사용자: userId={}", event.userId());
                return;
            }
            
            String defaultNickname = extractNicknameFromEmail(event.email());
            
            User user = User.create(
                    event.userId(),
                    defaultNickname,
                    event.email()
            );
            
            userRepository.save(user);
            log.info("사용자 생성 완료: userId={}, nickname={}", user.getUserId(), user.getNickname());
            
        } catch (JsonProcessingException e) {
            log.error("사용자 등록 이벤트 파싱 실패: {}", eventJson, e);
        } catch (Exception e) {
            log.error("사용자 등록 이벤트 처리 실패: {}", eventJson, e);
        }
    }

    @KafkaListener(topics = "grewmeet.user.nickname.changed", groupId = "recording-command-service")
    public void handleNicknameChanged(String eventJson) {
        try {
            log.info("닉네임 변경 이벤트 수신: {}", eventJson);
            
            UserNicknameChangedEvent event = objectMapper.readValue(eventJson, UserNicknameChangedEvent.class);
            
            User user = userRepository.findByUserId(event.userId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + event.userId()));
            
            user.changeNickname(event.newNickname());
            userRepository.save(user);
            log.info("닉네임 업데이트 완료: userId={}", event.userId());
            
        } catch (JsonProcessingException e) {
            log.error("닉네임 변경 이벤트 파싱 실패: {}", eventJson, e);
        } catch (Exception e) {
            log.error("닉네임 변경 이벤트 처리 실패: {}", eventJson, e);
        }
    }
    
    private String extractNicknameFromEmail(String email) {
        return email.substring(0, email.indexOf('@'));
    }
}