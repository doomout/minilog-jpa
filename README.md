# MiniLog

Spring Boot와 JPA를 기반으로 간단한 MiniLog 애플리케이션을 구현하며 REST API, GraphQL, 데이터베이스 연동, 인증 및 인가, 테스트, Docker 배포 환경을 학습하기 위한 프로젝트입니다.

『스프링부트 개발자 온보딩 가이드』를 기반으로 학습하며, 개발 과정에서 발생한 버전 호환성 문제와 해결 방법도 함께 기록합니다.

## 1. 개발 환경

| 구분 | 환경 |
|---|---|
| Java | 21 |
| Spring Boot | 3.3.1 |
| Gradle | 8.8 (Gradle Wrapper) |
| OS | Ubuntu 24.04 (WSL2) |
| IDE | IntelliJ IDEA |
| Container | Docker Desktop |
| Database | MySQL 8.0 |
| API 문서 | Swagger UI, GraphiQL |

## 2. 주요 기술

- **Spring Web**: REST API 구현
- **Spring Data JPA**: ORM 기반 데이터베이스 연동
- **Spring Security**: 인증 및 인가 처리
- **JWT**: 토큰 기반 사용자 인증
- **Spring for GraphQL**: GraphQL Query 및 Mutation 구현
- **MySQL**: 사용자, 게시글, 팔로우 데이터 저장
- **Lombok**: 반복적인 Java 코드 자동 생성
- **Swagger UI**: REST API 문서 확인 및 테스트
- **GraphiQL**: GraphQL API 테스트
- **JUnit 5**: 테스트 코드 작성
- **Testcontainers**: Docker 기반 통합 테스트
- **Spotless**: Java 코드 포맷 자동화
- **Docker**: 애플리케이션 이미지 빌드

## 3. 주요 기능

### 사용자 관리

- 사용자 생성 및 조회
- 사용자 정보 수정 및 삭제
- Spring Security 기반 인증 및 인가
- JWT 토큰 발급 및 검증

### 게시글 관리

- 게시글 생성, 조회, 수정, 삭제
- 사용자별 게시글 목록 조회
- 팔로우 관계를 이용한 피드 조회

### 팔로우 관리

- 사용자 팔로우 및 언팔로우
- 팔로우 목록 조회

### GraphQL API

REST API와 동일한 Service 계층을 재사용하여 GraphQL API를 구현합니다.

**Query (조회)**

- `getArticles`
- `getArticle`
- `getFeedList`
- `getFollowList`
- `getUsers`
- `getUserById`

**Mutation (데이터 변경)**

- `createArticle`
- `updateArticle`
- `deleteArticle`
- `follow`
- `unfollow`
- `createUser`
- `updateUser`
- `deleteUser`

GraphQL 스키마는 `src/main/resources/graphql/schema.graphqls`에 정의합니다.

기존 DTO와 GraphQL 응답 객체 사이의 변환은 `DtoGraphqlMapper`에서 처리합니다.

## 4. 프로젝트 구조

주요 패키지는 다음과 같습니다.

```text
src/main/java/com/asdf/minilog/
├── config/          # Spring 및 GraphQL 설정
├── controller/      # REST API Controller
├── dto/             # 요청 및 응답 DTO
├── entity/          # JPA Entity
├── graphql/         # GraphQL Query / Mutation Controller
│   ├── input/       # GraphQL 입력 객체
│   └── response/    # GraphQL 응답 객체
├── repository/      # Spring Data JPA Repository
├── security/        # Spring Security 및 JWT
├── service/         # 비즈니스 로직
└── util/            # DTO 변환 등 공통 기능
```

## 5. MySQL Docker 환경

개발용 MySQL 8.0 컨테이너를 생성합니다.

```bash
docker run --name mysql-minilog \
  -e MYSQL_ROOT_PASSWORD=dev_password \
  -e MYSQL_DATABASE=minilog_db \
  -e MYSQL_USER=minilog_user \
  -e MYSQL_PASSWORD=dev_password \
  -p 3307:3306 \
  -v mysql-minilog-data:/var/lib/mysql \
  -d mysql:8.0
```

위 비밀번호는 로컬 학습 환경을 위한 예시입니다. 실제 서비스에서는 별도의 안전한 비밀번호를 사용해야 합니다.

MySQL 컨테이너 실행 및 상태 확인:

```bash
docker start mysql-minilog
docker ps
```

개발 환경에서 MySQL은 호스트의 `3307` 포트로 접근할 수 있습니다.

Docker 컨테이너 내부에서 `localhost`는 해당 컨테이너 자신을 의미하므로, Spring Boot를 컨테이너로 실행할 때는 MySQL 연결 주소를 별도로 설정해야 합니다.

## 6. JWT 인증

Spring Security와 JWT를 이용하여 토큰 기반 인증을 구현합니다.

인증 처리 흐름:

```text
클라이언트
    ↓
Authorization: Bearer JWT
    ↓
JwtRequestFilter
    ↓
JWT 검증
    ↓
MinilogUserDetailsService
    ↓
SecurityContextHolder
    ↓
Controller → Service
```

JWT 비밀키 생성 예시:

```bash
openssl rand -base64 32
```

JWT 비밀키는 외부에 공개하지 않아야 하며, 운영 환경에서는 환경 변수 또는 별도의 비밀정보 관리 방식을 사용해야 합니다.

실제 비밀키가 포함된 설정 파일은 GitHub에 커밋하지 않습니다.

## 7. GraphQL 및 GraphiQL

GraphQL API 엔드포인트:

```text
POST http://localhost:8080/graphql
```

GraphiQL 테스트 페이지:

```text
http://localhost:8080/graphiql/index.html
```

Spring Boot 3.3.1에서 기본 제공되는 GraphiQL 페이지가 외부 CDN의 최신 GraphiQL 버전과 호환되지 않는 문제가 발생하여, 별도의 정적 HTML 페이지를 사용하도록 변경했습니다.

- 기본 제공 GraphiQL UI 비활성화
- `src/main/resources/static/graphiql/index.html` 사용
- GraphiQL 클라이언트 버전 `4.1.2` 고정

인증이 필요한 GraphQL 요청은 GraphiQL의 Headers 영역에 JWT를 입력합니다.

```json
{
  "Authorization": "Bearer YOUR_JWT_TOKEN"
}
```

## 8. Gradle 빌드 및 테스트

프로젝트에서 지정한 Gradle 버전을 사용하기 위해 Gradle Wrapper를 이용합니다.

```bash
# 테스트 실행
./gradlew test

# 전체 빌드
./gradlew clean build

# 실행 가능한 Spring Boot JAR 생성
./gradlew clean bootJar
```

빌드 결과물은 다음 디렉터리에 생성됩니다.

```text
build/libs/
```

현재 생성되는 실행 JAR 파일:

```text
minilog-0.0.1-dev.jar
```

전역 Gradle 대신 `./gradlew`를 사용하여 Spring Boot 3.3.1과 Gradle 8.8의 호환성을 유지합니다.

## 9. Docker 이미지 빌드

Spring Boot 애플리케이션을 Docker 이미지로 패키징합니다.

### Dockerfile

```dockerfile
# Java 21 실행 환경
FROM eclipse-temurin:21-jre-jammy

# 임시 디렉터리 볼륨 설정
VOLUME /tmp

# Gradle 빌드 결과물 지정
ARG JAR_FILE=build/libs/*.jar

# JAR 파일 복사
COPY ${JAR_FILE} /app.jar

# Spring Boot 프로파일
ENV SPRING_PROFILES_ACTIVE=prod

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

이미 컴파일된 JAR 파일을 실행하므로 Docker 이미지에는 JDK 대신 JRE를 사용합니다.

### 이미지 빌드

먼저 실행 가능한 JAR 파일을 생성합니다.

```bash
./gradlew clean bootJar
```

Docker 이미지를 생성합니다.

```bash
docker build -t minilog-graphql:latest .
```

이미지 생성 결과를 확인합니다.

```bash
docker images | grep minilog-graphql
```

## 10. 학습 과정에서 해결한 문제

### Spring Boot와 Gradle 버전 호환성

Spring Boot 3.3.1 프로젝트를 전역 Gradle 9.7로 빌드할 때 호환성 오류가 발생했습니다.

프로젝트의 Gradle Wrapper 8.8을 사용하여 해결했습니다.

### JPA Auditing 설정

`@CreatedDate`, `@LastModifiedDate` 필드가 자동으로 채워지지 않아 테스트에서 오류가 발생했습니다.

`@EnableJpaAuditing` 설정을 추가하여 해결했습니다.

### GraphiQL CDN 호환성

기본 GraphiQL 페이지에서 최신 JavaScript 리소스를 불러오지 못해 `Loading...` 화면이 지속되는 문제가 발생했습니다.

GraphiQL 4.1.2 버전을 사용하는 정적 HTML 페이지로 해결했습니다.

### Docker 베이스 이미지 오류

기존 Dockerfile에서 사용한 `openjdk:21-jdk-slim` 이미지 태그를 찾지 못하는 문제가 발생했습니다.

베이스 이미지를 `eclipse-temurin:21-jre-jammy`로 변경하여 해결했습니다.

### Docker JAR 파일 복사 오류

Docker 이미지 빌드 중 `build/libs` 디렉터리를 찾지 못하는 오류가 발생했습니다.

`./gradlew clean bootJar`로 JAR 파일을 먼저 생성한 후 Docker 이미지를 빌드하여 해결했습니다.

---

## 11. 향후 학습 계획

- Docker 컨테이너 실행 및 MySQL 연결
- REST 및 GraphQL API 통합 테스트
- Docker Compose를 이용한 애플리케이션과 데이터베이스 구성
- 개발 및 운영 환경 설정 분리
- 배포 환경 구성 및 개선

---

이 프로젝트는 Spring Boot의 주요 기능과 개발·테스트·배포 과정을 단계적으로 학습하기 위한 개인 학습 프로젝트입니다.