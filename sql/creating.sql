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
    cookie                VARCHAR(255) NOT NULL,
    FOREIGN KEY (commercial_network_id) REFERENCES commercial_network (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (city_id) REFERENCES city (id) ON UPDATE CASCADE ON DELETE RESTRICT
)
    ENGINE = InnoDB
    CHARSET = UTF8;

CREATE TABLE product_information
(
    id                    BIGINT PRIMARY KEY AUTO_INCREMENT,
    name                  VARCHAR(255) NOT NULL,
    url                   VARCHAR(512) NOT NULL UNIQUE,
    commercial_network_id BIGINT       NOT NULL,
    FOREIGN KEY (commercial_network_id) REFERENCES commercial_network (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX (url)
)
    ENGINE = InnoDB
    CHARSET = UTF8;

CREATE TABLE product_price
(
    id                       BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id               BIGINT         NOT NULL,
    shop_id                  BIGINT         NOT NULL,
    price                    DECIMAL(10, 2) NOT NULL,
    discount                 DOUBLE,
    price_with_discount      DECIMAL(10, 2),
    is_in_stock              BOOLEAN        NOT NULL,
    availability_information VARCHAR(255),
    date                     DATETIME       NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product_information (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (shop_id) REFERENCES shop (id) ON UPDATE CASCADE ON DELETE RESTRICT
)
    ENGINE = InnoDB
    CHARSET = UTF8;

CREATE TABLE product_price_lenta
(
    id              BIGINT PRIMARY KEY,
    price_with_card DECIMAL(10, 2),
    FOREIGN KEY (id) REFERENCES product_price (id) ON UPDATE CASCADE ON DELETE CASCADE
)
    ENGINE = InnoDB
    CHARSET = UTF8;

CREATE TABLE user_product
(
    id                    BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id               BIGINT  NOT NULL,
    product_price_id      BIGINT  NOT NULL,
    monitor_discount      BOOLEAN NOT NULL,
    monitor_availability  BOOLEAN NOT NULL,
    monitor_price_changes BOOLEAN NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (product_price_id) REFERENCES product_information (id) ON UPDATE CASCADE ON DELETE CASCADE,
    UNIQUE (user_id, product_price_id)
)
    ENGINE = InnoDB
    CHARSET = UTF8;