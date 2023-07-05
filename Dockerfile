FROM eclipse-temurin:8u372-b07-jre-jammy

ENV POSTGRES_USER=admin \
    POSTGRES_PASSWORD=admin

RUN mkdir -p /home/omniward

COPY target/classes /home/omniward/target/classes/

COPY target/omniward-*standalone.jar /home/omniward/target/omniward-standalone.jar

WORKDIR /home/omniward

CMD ["java", "-jar", "target/omniward-standalone.jar"]