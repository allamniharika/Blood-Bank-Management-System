FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM tomcat:10.1-jdk17-temurin
COPY --from=build /app/target/Blood-Bank-Management-System.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["sh", "-c", "sed -i 's/port=\"8080\"/port=\"'${PORT:-8080}'\"/' $CATALINA_HOME/conf/server.xml && catalina.sh run"]
