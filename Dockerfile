# 使用 OpenJDK 镜像作为基础镜像
FROM openjdk:11-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制构建好的 JAR 文件到容器中
COPY target/aigc-pms-web-0.0.1-SNAPSHOT.jar /app/aigc-pms-web-springboot.jar

# 设置容器的默认命令
CMD ["java", "-jar", "aigc-pms-web-springboot.jar"]
