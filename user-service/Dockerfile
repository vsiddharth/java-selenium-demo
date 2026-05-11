FROM eclipse-temurin:17-jre
WORKDIR /app
COPY user-service/target/user-service-1.0.0.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
