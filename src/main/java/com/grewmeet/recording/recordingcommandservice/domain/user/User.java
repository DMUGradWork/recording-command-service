package com.grewmeet.recording.recordingcommandservice.domain.user;

import com.grewmeet.recording.recordingcommandservice.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String email;

    public static User create(String userId, String nickname, String email) {
        validateUserId(userId);
        validateNickname(nickname);
        validateEmail(email);
        
        User user = new User();
        user.userId = userId;
        user.nickname = nickname;
        user.email = email;
        return user;
    }

    public void changeNickname(String newNickname) {
        validateNickname(newNickname);
        
        if (this.nickname.equals(newNickname)) {
            throw new IllegalArgumentException("동일한 닉네임으로 변경할 수 없습니다.");
        }
        
        this.nickname = newNickname;
    }

    public void changeEmail(String newEmail) {
        validateEmail(newEmail);
        
        if (this.email != null && this.email.equals(newEmail)) {
            throw new IllegalArgumentException("동일한 이메일로 변경할 수 없습니다.");
        }
        
        this.email = newEmail;
    }

    public boolean isValidForAttendance() {
        return userId != null && nickname != null;
    }

    private static void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        if (!userId.startsWith("user_")) {
            throw new IllegalArgumentException("잘못된 사용자 ID 형식입니다.");
        }
    }

    private static void validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        }
        if (nickname.length() > 20) {
            throw new IllegalArgumentException("닉네임은 20자를 초과할 수 없습니다.");
        }
        if (nickname.contains(" ")) {
            throw new IllegalArgumentException("닉네임에 공백은 포함될 수 없습니다.");
        }
    }

    private static void validateEmail(String email) {
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("잘못된 이메일 형식입니다.");
        }
    }
}