FROM openjdk:11
COPY ./target/csv-processor-*.jar /usr/src/csv-processor/csv-processor.jar
WORKDIR /usr/src/csv-processor
CMD ["java", "-jar", "csv-processor.jar"]