FROM eclipse-temurin:17-jre
WORKDIR /app
COPY order-service/target/order-service-1.0.0.jar /app/app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
