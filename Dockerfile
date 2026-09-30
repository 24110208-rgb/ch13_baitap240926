# Bước 1: Build dự án với Maven
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Bước 2: Chạy ứng dụng với Tomcat / Java
FROM openjdk:17-jdk-slim
COPY --from=build /target/*.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["java", "-jar", "/target/ch13_baitap240926-1.0-SNAPSHOT.jar"]
