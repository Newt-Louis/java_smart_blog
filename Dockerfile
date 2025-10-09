FROM ubuntu:latest
LABEL authors="Admin"

# Bước 1: Dùng image Java chính thức
FROM eclipse-temurin:21-jdk-alpine

# Bước 2: Tạo thư mục làm việc trong container
WORKDIR /app

# Bước 3: Copy file JAR đã build vào container
COPY target/*.jar app.jar

# Bước 4: Expose cổng 8080 (Spring Boot mặc định)
EXPOSE 8080

# Bước 5: Câu lệnh chạy app
ENTRYPOINT ["java", "-jar", "app.jar"]
