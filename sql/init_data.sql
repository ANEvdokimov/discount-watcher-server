SET search_path TO discount_watcher_schema;

INSERT INTO city (id, name, cyrillic_name)
VALUES (nextval('city_sequence'), 'Omsk', 'Омск');
INSERT INTO city (id, name, cyrillic_name)
VALUES (nextval('city_sequence'), 'Orenburg', 'Оренбург');

INSERT INTO shop_chain (id, name, cyrillic_name)
VALUES (nextval('shop_chain_sequence'), 'Lenta', 'Лента');

INSERT INTO shop (id, shop_chain_id, name, city_id, address, cookie)
VALUES (nextval('shop_sequence'),
        (SELECT id FROM shop_chain WHERE name = 'Lenta'),
        'Лента на Лукашевича',
        (SELECT id FROM city WHERE name = 'Omsk'),
        'ул. Лукашевича, д. 33',
        'Store:0079');
