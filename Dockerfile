FROM openjdk:11-jdk AS builder
WORKDIR /opt/springblog
COPY . .
RUN chmod +x ./gradlew && ./gradlew bootJar

FROM openjdk:11-jdk
WORKDIR /opt/springblog
COPY --from=builder /opt/springblog/build/libs/springblog-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "springblog-0.0.1-SNAPSHOT.jar"]
