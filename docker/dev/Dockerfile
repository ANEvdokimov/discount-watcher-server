FROM openjdk:18

ARG key_store_dir=/usr/keys/
ARG key_store_name=discount-watcher-server.p12

ENV DB_HOST host.docker.internal
ENV DB_PORT 3306
ENV KEY_STORE_PATH ${key_store_dir}${key_store_name}
ENV KEY_STORE_PASSWORD password
ENV KEY_STORE_ALIAS DWS

RUN mkdir ${key_store_dir}

ADD ./dev-certificates ${key_store_dir}
