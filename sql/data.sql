USE `discount-watcher-schema`;

INSERT INTO city (name, cyrillic_name) VALUE ('Omsk', 'Омск');
INSERT INTO city (name, cyrillic_name) VALUE ('Orenburg', 'Оренбург');

INSERT INTO commercial_network (name, cyrillic_name) VALUE ('Lenta', 'Лента');

INSERT INTO shop (commercial_network_id, name, city_id, address)
VALUES (1, 'Лента на Лукашевича', 1, 'ул. Лукашевича, д. 33')