FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.war app.war
EXPOSE 9090
ENTRYPOINT ["java","-jar","app.war"]