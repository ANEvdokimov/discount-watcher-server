SET search_path TO discount_watcher_schema;

INSERT INTO city (id, name, cyrillic_name)
VALUES (nextval('city_sequence'), 'Omsk', 'Омск');
INSERT INTO city (id, name, cyrillic_name)
VALUES (nextval('city_sequence'), 'Orenburg', 'Оренбург');

INSERT INTO shop_chain (id, name, cyrillic_name)
VALUES (nextval('shop_chain_sequence'), 'Magnit', 'Магнит');

INSERT INTO shop (id, shop_chain_id, name, city_id, address, cookie)
VALUES (nextval('shop_sequence'),
        (SELECT id FROM shop_chain WHERE name = 'Magnit'),
        'Магнит Семейный на Перелета',
        (SELECT id FROM city WHERE name = 'Omsk'),
        'ул. Перелета 32/1',
        'shopCode=992301; shopId=4268;');
