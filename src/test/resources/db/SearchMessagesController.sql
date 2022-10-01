INSERT INTO T_MESSAGE(id, reference_id, tenant_id, device_id, conversation_id, sender_id, recipient_id, created, timestamp, text)
    VALUES
        (100, '100', 1, 'iPhone100', '100,101', 100, 101, now() - INTERVAL '2 day', 800, 'e2-e4'),
        (101, '101', 1, 'iPhone101', '100,101', 101, 100, now() - INTERVAL '1 day', 900, 'e7-e5'),
        (102, '102', 1, 'iPhone100', '100,101', 100, 101, now(), 1000, 'd2-d4'),

        (200, '200', 1, 'iPhone100', '100,200', 100, 200, now() - INTERVAL '3 day', 700, 'Hello friend'),

        (300, '300', 1, 'iPhone300', '300,301', 300, 301, now(), 1000, 'Hello friend'),

        (400, '400', 1, 'iPhone100', '100,400', 100, 400, now(), 1000, 'Yo!');
