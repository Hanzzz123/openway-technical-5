FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

COPY pom.xml testng.xml ./
COPY src ./src

RUN mvn clean package -DskipTests

CMD ["mvn", "test"]
