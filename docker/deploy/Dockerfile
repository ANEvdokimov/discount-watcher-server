FROM openjdk:19

ARG key_store_dir=/usr/keys/
ARG key_store_name=discount-watcher-server.p12

ENV RABBIT_HOST host.docker.internal
ENV DB_HOST host.docker.internal
ENV DB_PORT 5432
ENV KEY_STORE_PATH ${key_store_dir}${key_store_name}
ENV KEY_STORE_PASSWORD password
ENV KEY_STORE_ALIAS DWS

RUN mkdir ${key_store_dir}

COPY ./target/*.jar ~/app.jar
COPY ./dev-certificates/*.p12 ${key_store_dir}${key_store_name}

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "~/app.jar"]