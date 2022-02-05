FROM jbiancot/openjdk-8-jre-headless
COPY portable-start/target/*.jar app-web.jar
CMD ["sh", "-c", "java ${JAVA_OPTS} -jar /app-web.jar"]
