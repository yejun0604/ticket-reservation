
# 1. Java 21 JDK를 사용하는 빌드 단계
FROM eclipse-temurin:21-jdk AS build

# 2. Docker 이미지 안의 작업 폴더
WORKDIR /app

# 3. 프로젝트 소스와 Gradle 파일 복사
COPY . .

# 4. Gradle Wrapper 실행 권한 부여
RUN chmod +x ./gradlew

# 5. Spring Boot 실행용 JAR 빌드
RUN ./gradlew bootJar --no-daemon

# 6. Java 21 실행 환경으로 최종 이미지 생성
FROM eclipse-temurin:21-jre

# 7. 최종 이미지 작업 폴더
WORKDIR /app

# 8. 빌드된 JAR 파일만 가져오기
COPY --from=build /app/build/libs/*.jar app.jar

# 9. Spring Boot 사용 포트
EXPOSE 8080

# 10. 컨테이너 시작 시 Spring Boot 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
