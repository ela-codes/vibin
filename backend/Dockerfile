# dockerfile will be built on java21
FROM openjdk:21
EXPOSE 8080
ADD target/vibin-0.0.1-SNAPSHOT.jar vibin-app.jar
ENTRYPOINT ["java", "-jar", "vibin-app.jar"]
