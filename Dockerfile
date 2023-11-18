FROM openjdk:21

RUN groupadd --gid 2000 dws \
  && useradd --uid 2000 --gid dws --shell /bin/bash --create-home dws

COPY ./application/build/libs/*.jar /app/discount_watcher_server.jar
RUN chmod 555 /app/discount_watcher_server.jar

USER dws

ENTRYPOINT ["java", "-Dspring.profiles.active=production", "-jar", "/app/discount_watcher_server.jar"]
