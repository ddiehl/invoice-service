FROM openjdk:15-alpine
LABEL mantainer="Daniel Diehl"

# Prevent running as root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG JAR_FILE
RUN echo ${JAR_FILE}
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
