USE `discount-watcher-schema`;

INSERT INTO city (name, cyrillic_name) VALUE ('Omsk', 'Омск');
INSERT INTO city (name, cyrillic_name) VALUE ('Orenburg', 'Оренбург');

INSERT INTO shop_chain (name, cyrillic_name) VALUE ('Lenta', 'Лента');

INSERT INTO shop (shop_chain_id, name, city_id, address, cookie)
VALUES (1, 'Лента на Лукашевича', 1, 'ул. Лукашевича, д. 33', 'Store:0079')