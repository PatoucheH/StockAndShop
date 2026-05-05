# FIRST

# COMPILATION 
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app

#Copy maven
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Make executable
RUN chmod +x mvnw && sed -i 's/\r$//' mvnw

# DL dependencies
RUN ./mvnw dependency:go-offline -q

#COPY sources and compile
COPY src ./src
RUN ./mvnw package -DskipTests -q

# SECOND

# EXECUTION
FROM eclipse-temurin:25-jre

# GEt .jar
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]


