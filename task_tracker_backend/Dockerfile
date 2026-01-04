#parent image
FROM eclipse-temurin:17-jdk-alpine

# working directory for inside container 
WORKDIR /app

#maven wrapper and pom file copy
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN tr -d '\r' < mvnw > mvnw_unix && mv mvnw_unix mvnw
RUN chmod +x mvnw


RUN ./mvnw dependency:go-offline

#copy source code
COPY src ./src

#build
RUN ./mvnw clean package -DskipTests

#runjar file
ENTRYPOINT ["java","-jar","target/app.jar"]