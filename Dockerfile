# 多阶段构建 - 第一阶段：构建应用
FROM maven:3.9.6-eclipse-temurin-21 AS build

# 设置工作目录
WORKDIR /app

# 复制Maven配置文件
COPY pom.xml .
COPY dependency-reduced-pom.xml* ./

# 下载依赖（Docker缓存优化）
RUN mvn dependency:go-offline -B

# 复制源代码和配置文件
COPY src ./src
COPY config.yml .

# 构建应用（跳过测试以加快构建速度）
RUN mvn clean package -DskipTests

# 第二阶段：运行环境
FROM eclipse-temurin:21-jre-alpine

# 安装必要的包
RUN apk add --no-cache curl

# 创建非root用户
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# 设置工作目录
WORKDIR /app

# 从构建阶段复制JAR文件
COPY --from=build /app/target/tech-challenge-1.0-SNAPSHOT.jar app.jar
COPY --from=build /app/config.yml config.yml

# 更改文件所有权
RUN chown -R appuser:appgroup /app

# 切换到非root用户
USER appuser

# 暴露端口
EXPOSE 8080 8081

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8081/healthcheck || exit 1

# 启动命令
CMD ["java", "-jar", "app.jar", "server", "config.yml"] 