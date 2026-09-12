# MiniLog JPA

Spring Boot와 JPA를 기반으로 간단한 MiniLog 애플리케이션을 구현하며  
웹 API, 데이터베이스 연동, 테스트 환경 구성을 학습하기 위한 프로젝트입니다.

## 개발 환경

- Java 21
- Spring Boot 3.3.1
- Gradle 8.8
- Ubuntu 24.04 (WSL2)
- IntelliJ IDEA
- Docker Desktop
- MySQL 8.0

## 주요 기술

- Spring Boot
- Spring Web
- Spring Data JPA
- MySQL
- Lombok
- Swagger UI
- Testcontainers
- JUnit 5
- Spotless
- Spring Security 

## 주요 의존성

- `spring-boot-starter-web`
    - REST API 및 웹 애플리케이션 개발

- `spring-boot-starter-data-jpa`
    - JPA 기반 데이터베이스 처리

- `mysql-connector-java`
    - MySQL JDBC 드라이버

- `springdoc-openapi-starter-webmvc-ui`
    - Swagger UI 및 OpenAPI 문서 제공

- `lombok`
    - Getter, Setter 등의 반복 코드 자동 생성

- `spring-boot-starter-test`
    - Spring Boot 테스트 지원

- `testcontainers`
    - 테스트 실행 시 임시 MySQL 컨테이너를 생성하여 테스트 환경 구성

- `spotless`
    - Java 코드 포맷 자동화
  - 'spring-boot-starter-security'
    - 스프링 시큐리티 

## MySQL Docker 환경

MiniLog 전용 MySQL 컨테이너를 실행합니다.

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
```bash
// jwt 생성하고 검증할 때 사용할 비밀키 생성 명령어
openssl rand -base64 32
```
