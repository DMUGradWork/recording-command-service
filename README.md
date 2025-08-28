# Recording Command Service

기록 도메인의 명령 서비스 (출석 기록, 데이팅 서비스 참여, 스터디모임 참여 기록 생성, 수정, 삭제)

## 🔧 환경 설정
- **포트**: 8080 (기본값)
- **데이터베이스**: H2 (인메모리)
- **Kafka**: localhost:9092

## 📡 이벤트 스키마
CQRS Command Side로서 모든 상태 변경 시 Outbox 패턴을 통해 이벤트 발행

### 🔽 수신 이벤트 (Incoming Saga Events)

<details>
<summary><strong>ParticipantJoinedEvent</strong> - Dating Service에서 수신</summary>

**Record 클래스:**
```java
// 📁 saga/incoming/DatingParticipationReceived.java
public record DatingParticipationReceived(
    Long datingMeetingId,    // 데이팅 이벤트 ID
    Long participantId,      // 참가자 ID  
    Long userId,             // 사용자 ID
    LocalDateTime joinedAt   // 참여 시점
) {}
```

**예시 JSON:**
```json
{
  "datingMeetingId": 123,
  "participantId": 456,
  "userId": 789,
  "joinedAt": "2024-08-21T19:00:00"
}
```
</details>

<details>
<summary><strong>StudyMeetingParticipationComplete</strong> - Study Service에서 수신</summary>

**Record 클래스:**
```java
// 📁 saga/incoming/StudyParticipationReceived.java
public record StudyParticipationReceived(
    Long studyMeetingId,     // 스터디 미팅 ID
    Long participantId,      // 참가자 ID
    Long userId,             // 사용자 ID  
    LocalDateTime completedAt // 완료 시점
) {}
```

**예시 JSON:**
```json
{
  "studyMeetingId": 123,
  "participantId": 456,
  "userId": 789,
  "completedAt": "2024-08-21T14:00:00"
}
```
</details>

### 🔼 발행 이벤트 (Outgoing Events)

<details>
<summary><strong>AttendanceRecorded</strong> - Query Service로 발행</summary>

**이벤트 정보:**
- **이벤트 타입:** AttendanceRecorded  
- **Aggregate 타입:** AttendanceRecord
- **발생 시점:** POST /attendance (출석 체크 버튼 클릭 시)
- **대상 서비스:** Recording Query Service

**Record 클래스:**
```java
// 📁 saga/outgoing/AttendanceRecordCreated.java
public record AttendanceRecordCreated(
    Long id,                        // 출석 기록 ID
    String userId,                  // 사용자 ID
    LocalDate attendanceDate,       // 출석 날짜
    Boolean isAttended,             // 출석 여부
    LocalDateTime checkInTime,      // 체크인 시간
    LocalDateTime recordedAt        // 기록 생성 시간
) {
    public static AttendanceRecordCreated from(AttendanceRecord record) {
        return new AttendanceRecordCreated(
            record.getId(),
            record.getUserId(),
            record.getAttendanceDate(),
            record.getIsAttended(),
            record.getCheckInTime(),
            record.getRecordedAt()
        );
    }
}
```

**예시 JSON:**
```json
{
  "eventType": "AttendanceRecorded",
  "aggregateType": "AttendanceRecord",
  "aggregateId": 123,
  "data": {
    "id": 123,
    "userId": "user_123456789",
    "attendanceDate": "2024-08-21",
    "isAttended": true,
    "checkInTime": "2024-08-21T09:00:00",
    "recordedAt": "2024-08-21T09:00:00"
  }
}
```
</details>

<details>
<summary><strong>DatingParticipationRecorded</strong> - Query Service로 발행</summary>

**이벤트 정보:**
- **이벤트 타입:** DatingParticipationRecorded
- **Aggregate 타입:** DatingParticipation  
- **발생 시점:** 외부 이벤트 수신 시 (ParticipantJoinedEvent 처리)
- **대상 서비스:** Recording Query Service

**Record 클래스:**
```java
// 📁 saga/outgoing/DatingParticipationRecorded.java
public record DatingParticipationRecorded(
    Long id,                            // 참여 기록 ID
    String userId,                      // 사용자 ID
    String datingEventId,               // 데이팅 이벤트 ID
    LocalDateTime participationDate,    // 참여 날짜
    String status,                      // 참여 상태
    LocalDateTime recordedAt            // 기록 생성 시간
) {}
```

**예시 JSON:**
```json
{
  "eventType": "DatingParticipationRecorded",
  "aggregateType": "DatingParticipation",
  "aggregateId": 456,
  "data": {
    "id": 456,
    "userId": "user_123456789",
    "datingEventId": "dating_event_789",
    "participationDate": "2024-08-21T19:00:00",
    "status": "PARTICIPATED",
    "recordedAt": "2024-08-21T19:00:00"
  }
}
```
</details>

<details>
<summary><strong>StudyGroupParticipationRecorded</strong> - Query Service로 발행</summary>

**이벤트 정보:**
- **이벤트 타입:** StudyGroupParticipationRecorded
- **Aggregate 타입:** StudyGroupParticipation
- **발생 시점:** 외부 이벤트 수신 시 (StudyMeetingParticipationComplete 처리)  
- **대상 서비스:** Recording Query Service

**Record 클래스:**
```java
// 📁 saga/outgoing/StudyParticipationRecorded.java
public record StudyParticipationRecorded(
    Long id,                            // 참여 기록 ID
    String userId,                      // 사용자 ID
    String studyGroupId,                // 스터디 그룹 ID
    String sessionId,                   // 세션 ID
    LocalDateTime participationDate,    // 참여 날짜
    String status,                      // 참여 상태
    LocalDateTime recordedAt            // 기록 생성 시간
) {}
```

**예시 JSON:**
```json
{
  "eventType": "StudyGroupParticipationRecorded",
  "aggregateType": "StudyGroupParticipation",
  "aggregateId": 789,
  "data": {
    "id": 789,
    "userId": "user_123456789",
    "studyGroupId": "study_group_456",
    "sessionId": "session_123",
    "participationDate": "2024-08-21T14:00:00",
    "status": "ATTENDED",
    "recordedAt": "2024-08-21T14:00:00"
  }
}
```
</details>

## 🚀 실행
```bash
./gradlew bootRun
```

## 📋 API 엔드포인트

### 출석 기록 관리
- **POST** `/attendance` - 새 출석 기록 생성 (출석 체크 버튼 클릭 시 호출)

### 데이팅 서비스 참여 기록 관리
- **POST** `/dating-participations` - 데이팅 서비스 참여 기록 생성
- **PATCH** `/dating-participations/{participationId}` - 참여 상태 수정
- **DELETE** `/dating-participations/{participationId}` - 참여 기록 삭제

### 스터디모임 참여 기록 관리
- **POST** `/study-participations` - 스터디모임 참여 기록 생성
- **PATCH** `/study-participations/{participationId}` - 참여 상태 수정
- **DELETE** `/study-participations/{participationId}` - 참여 기록 삭제

## 📄 API 문서
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

## 🏗️ 아키텍처
- **CQRS Command Side** (쓰기 전용, 복잡한 조회 로직 없음)
- **Event-Driven Architecture** (Kafka 기반 이벤트 수신/발행)
- **Event Listener Pattern** (외부 서비스 이벤트 구독 처리)
- **Outbox Pattern** (DB 트랜잭션 내 이벤트 기록)
- **Domain-Driven Design** 기반 구조

## 🔒 핵심 제약사항
- 모든 서비스 메서드는 `@Transactional` 필수
- 복잡한 조회 기능은 수행하지 않음 (Query Service 역할 아님)
- 상태 변경과 Outbox 이벤트 기록의 원자성 보장

## 📊 도메인 모델

### 기록 유형
- **출석 기록 (Attendance)**: 출석 체크 버튼을 통한 일일 출석 여부 기록
- **데이팅 서비스 참여 기록 (Dating Participation)**: 매칭, 데이트 활동 등의 참여 이력
- **스터디모임 참여 기록 (Study Group Participation)**: 스터디 그룹 가입, 세션 참여 등의 활동

### 출석 상태
- `true`: 출석 (출석 체크 버튼 클릭함)
- `false`: 미출석 (출석 체크 안함)

## 🛠️ 기술 스택
- **Java 21** & Spring Boot 3.5.4
- **Spring Data JPA** & H2 Database (개발용)
- **Apache Kafka** Event Streaming
- **Lombok** 보일러플레이트 코드 감소
- **JUnit 5** Testing
- **Gradle** Build Management