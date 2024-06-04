FROM gradle:7.6-jdk17-alpine as webapp_build
WORKDIR /
COPY . .
RUN gradle assemble

FROM openjdk:17-alpine
COPY --from=webapp_build build/libs/Splitter-0.0.1-SNAPSHOT.jar Splitter.jar
ENTRYPOINT ["java", "-jar", "/Splitter.jar"]
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://database:5432/jdbc