FROM openjdk:8
EXPOSE 8091
ADD target/deposit.jar deposit.jar
ENTRYPOINT ["java", "-jar", "/deposit.jar"]



