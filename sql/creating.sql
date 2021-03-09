DROP SCHEMA IF EXISTS `discount-watcher-schema`;
CREATE SCHEMA `discount-watcher-schema`;
USE `discount-watcher-schema`;

CREATE TABLE user
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    login         VARCHAR(255)        NOT NULL UNIQUE,
    name          VARCHAR(255) BINARY NOT NULL,
    password      VARCHAR(255) BINARY NOT NULL,
    register_date DATETIME            NOT NULL,
    role          ENUM ('ROLE_USER')  NOT NULL DEFAULT 'ROLE_USER',
    enabled       BOOLEAN             NOT NULL DEFAULT TRUE
)
    ENGINE = InnoDB
    CHARSET = UTF8;

CREATE TABLE city
(
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
)
    ENGINE = InnoDB
    CHARSET = UTF8;

CREATE TABLE commercial_network
(
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
)
    ENGINE = InnoDB
    CHARSET = UTF8;

CREATE TABLE shop
(
    id                    BIGINT PRIMARY KEY AUTO_INCREMENT,
    commercial_network_id BIGINT       NOT NULL,
    name                  VARCHAR(255) NOT NULL,
    city_id               BIGINT       NOT NULL,
    address               VARCHAR(255) NOT NULL,
    FOREIGN KEY (commercial_network_id) REFERENCES commercial_network (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (city_id) REFERENCES city (id) ON UPDATE CASCADE ON DELETE RESTRICT
)
    ENGINE = InnoDB
    CHARSET = UTF8;

CREATE TABLE product
(
    id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    name    VARCHAR(255) NOT NULL,
    shop_id BIGINT       NOT NULL,
    url     VARCHAR(512) NOT NULL UNIQUE,
    FOREIGN KEY (shop_id) REFERENCES shop (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    UNIQUE (shop_id, url)
)
    ENGINE = InnoDB
    CHARSET = UTF8;

CREATE TABLE product_price
(
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id          BIGINT   NOT NULL,
    price               FLOAT    NOT NULL,
    discount            FLOAT,
    price_with_discount FLOAT,
    date                DATETIME NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product (id) ON UPDATE CASCADE ON DELETE RESTRICT
)
    ENGINE = InnoDB
    CHARSET = UTF8;

CREATE TABLE product_price_lenta
(
    id              BIGINT PRIMARY KEY,
    price_with_card FLOAT NOT NULL,
    FOREIGN KEY (id) REFERENCES product_price (id) ON UPDATE CASCADE ON DELETE CASCADE
)
    ENGINE = InnoDB
    CHARSET = UTF8;