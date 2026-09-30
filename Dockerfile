# Bước 1: Build dự án với Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Bước 2: Chạy ứng dụng với Java thế hệ mới (Eclipse Temurin)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/ch13_baitap240926-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
