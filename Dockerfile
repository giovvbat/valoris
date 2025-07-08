#bringing jdk into container
FROM eclipse-temurin:17

#copying .jar file to container
COPY ./target/valoris-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

#executing .jar file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
