FROM openjdk:11
COPY ./target/firestore-connector-*.jar /usr/src/firestore-connector/firestore-connector.jar
WORKDIR /usr/src/firestore-connector
CMD ["java", "-jar", "firestore-connector.jar"]