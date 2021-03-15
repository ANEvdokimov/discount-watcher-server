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
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    cyrillic_name VARCHAR(255) NOT NULL
)
    ENGINE = InnoDB
    CHARSET = UTF8;

CREATE TABLE commercial_network
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    cyrillic_name VARCHAR(255) NOT NULL
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

CREATE TABLE product_information_lenta
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    url         VARCHAR(512) NOT NULL UNIQUE,
    vendor_code VARCHAR(64)
)
    ENGINE = InnoDB
    CHARSET = UTF8;

CREATE TABLE product_price_lenta
(
    id                       BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id               BIGINT   NOT NULL,
    price                    FLOAT    NOT NULL,
    price_with_card          FLOAT    NOT NULL,
    discount                 FLOAT,
    price_with_discount      FLOAT,
    is_in_stock              BOOLEAN  NOT NULL,
    availability_information VARCHAR(255),
    date                     DATETIME NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product_information_lenta (id) ON UPDATE CASCADE ON DELETE RESTRICT
)
    ENGINE = InnoDB
    CHARSET = UTF8;

CREATE TABLE user_product_lenta
(
    id                     BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id                BIGINT  NOT NULL,
    product_information_id BIGINT  NOT NULL,
    shop_id                BIGINT  NOT NULL,
    monitor_discount       BOOLEAN NOT NULL,
    monitor_availability   BOOLEAN NOT NULL,
    monitor_price_changes  BOOLEAN NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (product_information_id) REFERENCES product_information_lenta (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (shop_id) REFERENCES shop (id) ON UPDATE CASCADE ON DELETE CASCADE,
    UNIQUE (user_id, product_information_id, shop_id)
)
    ENGINE = InnoDB
    CHARSET = UTF8;