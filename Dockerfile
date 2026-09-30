# Bước 1: Build dự án với Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Bước 2: Deploy WAR lên Tomcat 10 (tương thích Jakarta EE 10)
FROM tomcat:10.1-jdk17-temurin

# Xóa app mặc định của Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR vào thư mục webapps với tên ROOT.war để chạy ở context "/"
COPY --from=build /app/target/ch13_baitap240926-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
