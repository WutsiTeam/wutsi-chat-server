INSERT INTO T_MESSAGE(id, tenant_id, device_id, conversation_id, sender_id, recipient_id, created, text)
    VALUES
        (100, 1, 'iPhone100', '100,101', 100, 101, now() - INTERVAL '2 day', 'e2-e4'),
        (101, 1, 'iPhone101', '100,101', 101, 100, now() - INTERVAL '1 day', 'e7-e5'),
        (102, 1, 'iPhone100', '100,101', 100, 101, now(), 'd2-d4'),

        (200, 1, 'iPhone100', '100,200', 100, 200, now() - INTERVAL '3 day', 'Hello friend'),

        (300, 1, 'iPhone300', '300,301', 300, 301, now(), 'Hello friend'),

        (400, 1, 'iPhone100', '100,400', 100, 400, now(), 'Yo!');
