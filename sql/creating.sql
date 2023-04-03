DROP SCHEMA IF EXISTS discount_watcher_schema CASCADE;
CREATE SCHEMA discount_watcher_schema;
SET search_path TO discount_watcher_schema;

CREATE TYPE user_role AS ENUM ('ROLE_USER');

CREATE SEQUENCE user_sequence;

CREATE TABLE "user"
(
    id            BIGINT PRIMARY KEY,
    login         VARCHAR(256) NOT NULL UNIQUE,
    name          VARCHAR(256) NOT NULL,
    register_date TIMESTAMP    NOT NULL,
    role          user_role    NOT NULL DEFAULT 'ROLE_USER',
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE SEQUENCE city_sequence;
CREATE TABLE city
(
    id            BIGINT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    cyrillic_name VARCHAR(255) NOT NULL
);

CREATE SEQUENCE shop_chain_sequence;
CREATE TABLE shop_chain
(
    id            BIGINT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    cyrillic_name VARCHAR(255) NOT NULL
);

CREATE SEQUENCE shop_sequence;
CREATE TABLE shop
(
    id            BIGINT PRIMARY KEY,
    shop_chain_id BIGINT       NOT NULL,
    name          VARCHAR(255) NOT NULL,
    city_id       BIGINT       NOT NULL,
    address       VARCHAR(255) NOT NULL,
    cookie        VARCHAR(255) NOT NULL,
    FOREIGN KEY (shop_chain_id) REFERENCES shop_chain (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (city_id) REFERENCES city (id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE SEQUENCE product_information_sequence;
CREATE TABLE product_information
(
    id   BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    url  VARCHAR(512) NOT NULL UNIQUE
);
CREATE INDEX ON product_information (url);

CREATE SEQUENCE product_sequence;
CREATE TABLE product
(
    id                     BIGINT PRIMARY KEY,
    product_information_id BIGINT NOT NULL,
    shop_id                BIGINT NOT NULL,
    UNIQUE (product_information_id, shop_id),
    FOREIGN KEY (product_information_id) REFERENCES product_information (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (shop_id) REFERENCES shop (id) ON UPDATE CASCADE ON DELETE RESTRICT
);
CREATE INDEX ON product (product_information_id, shop_id);

CREATE SEQUENCE product_price_sequence;
CREATE TABLE product_price
(
    id                       BIGINT PRIMARY KEY,
    product_id               BIGINT         NOT NULL,
    price                    DECIMAL(10, 2) NOT NULL,--TODO money
    discount                 DOUBLE PRECISION,
    price_with_discount      DECIMAL(10, 2),
    is_in_stock              BOOLEAN        NOT NULL,
    availability_information VARCHAR(255),
    date                     TIMESTAMP      NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product (id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE product_price_lenta
(
    id              BIGINT PRIMARY KEY,
    price_with_card DECIMAL(10, 2),--TODO money
    FOREIGN KEY (id) REFERENCES product_price (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE SEQUENCE user_product_sequence;
CREATE TABLE user_product
(
    id                    BIGINT PRIMARY KEY,
    user_id               BIGINT  NOT NULL,
    product_id            BIGINT  NOT NULL,
    monitor_discount      BOOLEAN NOT NULL,
    monitor_availability  BOOLEAN NOT NULL,
    monitor_price_changes BOOLEAN NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user" (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product (id) ON UPDATE CASCADE ON DELETE CASCADE,
    UNIQUE (user_id, product_id)
);
CREATE INDEX ON user_product (user_id, product_id);
