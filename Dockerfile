FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/arcatron.jar /arcatron/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/arcatron/app.jar"]
