# Recording Command Service

기록 도메인의 명령 서비스 (출석 기록 생성, 데이팅/스터디 참여 기록 저장)

## 🔧 환경 설정
- **포트**: 8083
- **데이터베이스**: MySQL 8.0
- **Kafka**: localhost:9092
- **인증**: JWT (X-Owner-Id 헤더)

## 🏗️ 아키텍처
- **CQRS Command Side** (쓰기 전용)
- **Event-Driven Architecture** (Kafka 기반)
- **Event Sourcing** (상태 변경 시 이벤트 발행)
- **Domain-Driven Design**

## 📡 이벤트 처리

### 🔽 수신 이벤트 (Incoming Events)

외부 서비스로부터 참여 완료 이벤트를 수신하여 기록 생성 후 Query Service로 재발행

<details>
<summary><strong>DatingParticipationReceived</strong> - Dating Service에서 수신</summary>

**Kafka Topic**: `grewmeet.dating.participation.completed`

**Record 클래스:**
```java
public record DatingParticipationReceived(
    UUID studyGroupId,              // 데이팅 그룹 ID
    UUID userId,                    // 사용자 ID
    UUID meetingId,                 // 미팅 ID
    String studyMeetingEventName,   // 이벤트명
    LocalDateTime when,             // 예정 시간
    LocalDateTime createdAt         // 생성 시간
) {}
```

**처리 과정:**
1. 이벤트 수신
2. 중복 체크 (`meetingId` + `userId`)
3. `DatingParticipation` 엔티티 저장 (MySQL)
4. `DatingParticipationRecorded` 이벤트 발행 → Query Service

</details>

<details>
<summary><strong>StudyParticipationReceived</strong> - Study Service에서 수신</summary>

**Kafka Topic**: `grewmeet.study.participation.completed`

**Record 클래스:**
```java
public record StudyParticipationReceived(
    UUID studyGroupId,          // 스터디 그룹 ID
    UUID userId,                // 사용자 ID
    UUID meetingId,             // 미팅 ID
    String studyGroupName,      // 스터디 그룹명
    String meetingName,         // 미팅명
    LocalDateTime completedAt   // 완료 시간
) {}
```

**처리 과정:**
1. 이벤트 수신
2. 중복 체크 (`meetingId` + `userId`)
3. `StudyParticipation` 엔티티 저장 (MySQL)
4. `StudyParticipationRecorded` 이벤트 발행 → Query Service

</details>

### 🔼 발행 이벤트 (Outgoing Events)

Query Service로 기록 이벤트 발행

<details>
<summary><strong>AttendanceRecordCreated</strong></summary>

**Kafka Topic**: `grewmeet.recording.attendance`

**Record 클래스:**
```java
public record AttendanceRecordCreated(
    Long id,                        // 출석 기록 ID
    String userId,                  // 사용자 ID
    LocalDateTime attendanceTime,   // 출석 시간
    LocalDateTime createdAt         // 기록 생성 시간
) {}
```

**발생 시점**: `POST /api/attendance` API 호출 시

</details>

<details>
<summary><strong>DatingParticipationRecorded</strong></summary>

**Kafka Topic**: `grewmeet.recording.dating-participation`

**Record 클래스:**
```java
public record DatingParticipationRecorded(
    Long id,                        // 참여 기록 ID
    UUID datingGroupId,             // 데이팅 그룹 ID
    UUID userId,                    // 사용자 ID
    UUID meetingId,                 // 미팅 ID
    String eventName,               // 이벤트명
    LocalDateTime scheduledAt,      // 예정 시간
    LocalDateTime meetingCreatedAt, // 미팅 생성 시간
    LocalDateTime recordedAt        // 기록 생성 시간
) {}
```

**발생 시점**: Dating Service 이벤트 수신 후 저장 완료 시

</details>

<details>
<summary><strong>StudyParticipationRecorded</strong></summary>

**Kafka Topic**: `grewmeet.recording.study-participation`

**Record 클래스:**
```java
public record StudyParticipationRecorded(
    Long id,                    // 참여 기록 ID
    UUID studyGroupId,          // 스터디 그룹 ID
    UUID userId,                // 사용자 ID
    UUID meetingId,             // 미팅 ID
    String studyGroupName,      // 스터디 그룹명
    String meetingName,         // 미팅명
    LocalDateTime completedAt,  // 완료 시간
    LocalDateTime recordedAt    // 기록 생성 시간
) {}
```

**발생 시점**: Study Service 이벤트 수신 후 저장 완료 시

</details>

## 📋 API 엔드포인트

### 출석 기록
- **POST** `/api/attendance` - 출석 기록 생성
  - Header: `X-Owner-Id` (JWT 인증)
  - Body: `{ "attendanceTime": "2025-01-03T10:00:00" }` (optional)
  - 제약: 같은 날짜에 중복 출석 불가

## 🚀 실행

### 로컬 개발
```bash
# MySQL 필요 (Docker Compose 사용)
docker-compose up -d mysql

# 서비스 시작
./gradlew bootRun
```

### Docker Compose로 전체 환경 실행
```bash
docker-compose up -d
```

## 📊 도메인 모델

### AttendanceRecord (출석 기록)
- `id`: Long (PK)
- `userId`: String (사용자 ID)
- `attendanceTime`: LocalDateTime (출석 시간)
- `createdAt`: LocalDateTime (생성 시간)

**비즈니스 규칙:**
- 같은 날짜(년월일)에 한 번만 출석 가능
- 출석 기록 = 출석함 (단순 존재 여부만 표현)

### DatingParticipation (데이팅 참여 기록)
- `id`: Long (PK)
- `datingGroupId`: UUID
- `userId`: UUID
- `meetingId`: UUID
- `eventName`: String
- `scheduledAt`: LocalDateTime
- `meetingCreatedAt`: LocalDateTime
- `recordedAt`: LocalDateTime

### StudyParticipation (스터디 참여 기록)
- `id`: Long (PK)
- `studyGroupId`: UUID
- `userId`: UUID
- `meetingId`: UUID
- `studyGroupName`: String
- `meetingName`: String
- `completedAt`: LocalDateTime
- `recordedAt`: LocalDateTime

## 🛠️ 기술 스택
- **Java 21** & Spring Boot 3.5.4
- **Spring Data JPA** & MySQL 8.0
- **Apache Kafka** Event Streaming
- **Lombok** 코드 간소화
- **Gradle** Build Tool

## 🔒 핵심 제약사항
- 출석은 수정/삭제 불가 (생성만 가능)
- 날짜 기반 중복 체크 (같은 날 중복 출석 방지)
- 모든 상태 변경 시 이벤트 발행 필수
- 복잡한 조회는 Query Service에서 처리
