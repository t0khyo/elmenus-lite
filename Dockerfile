# Stage 1: Build the Maven project using the official Maven image with Alpine
FROM openjdk:17-alpine AS source-build-stage

LABEL authors="Java Mentorship 6th Batch"

WORKDIR /app

# Copy the Maven wrapper and project files
COPY mvnw ./
COPY .mvn/ ./.mvn/
COPY pom.xml ./
COPY src/ ./src/


RUN chmod +x mvnw
# Build the project using the Maven wrapper
# Using Docker's cache mount feature (available in BuildKit)
RUN --mount=type=cache,target=/root/.m2/repository ./mvnw clean package -Dmaven.test.skip=true


# Create a small shell script to find and run the JAR
# Create entrypoint with proper newlines
RUN printf '#!/bin/sh\n\
    JAR_FILE=$(ls ./target/*.jar | grep -v ".original")\n\
    echo "Found JAR: $JAR_FILE"\n\
    exec java -jar "$JAR_FILE"\n' > /entrypoint.sh \
    && chmod +x /entrypoint.sh


# Run the Spring Boot APP using the dynamic JAR detection
ENTRYPOINT ["/bin/sh", "/entrypoint.sh"]
#CMD tail -f /dev/null
# Expose necessary ports
EXPOSE 8080