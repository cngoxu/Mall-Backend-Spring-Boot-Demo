# Build stage: 使用 Maven 3.8.8 和 OpenJDK 11 编译项目
FROM maven:3.8.8-eclipse-temurin-11-focal AS build

COPY . .
RUN mvn clean package spring-boot:repackage

# Production stage: 使用轻量级 OpenJDK 11 运行 jar 包
FROM openjdk:11-jre-slim

COPY --from=build /target/mall-1.0-SNAPSHOT.jar /app.jar

# 配置容器启动时的命令
ENTRYPOINT ["java", "-jar", "/app.jar"]

# 暴露端口
EXPOSE 8088