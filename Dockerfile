FROM maven:3.8.4-openjdk-17 AS build

# Thiết lập thư mục làm việc
WORKDIR /app

# Sao chép file pom.xml và cài đặt các dependency
COPY pom.xml .
RUN mvn dependency:go-offline

# Sao chép mã nguồn và build ứng dụng
COPY src ./src
RUN mvn clean package -DskipTests


# Sử dụng hình ảnh JDK để chạy ứng dụng
FROM openjdk:17-jdk-slim

# Copy file JAR từ giai đoạn build sang image runtime
COPY --from=build /app/target/gizmoshop-0.0.1-SNAPSHOT.jar /app/

EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "/app/gizmoshop-0.0.1-SNAPSHOT.jar"]