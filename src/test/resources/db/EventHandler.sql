INSERT INTO T_MESSAGE(id, reference_id, tenant_id, device_id, conversation_id, sender_id, recipient_id, created, timestamp, text)
    VALUES
        (100, '100', 1, 'iPhone100', '100,101', 100, 101, now() - INTERVAL '2 day', 800, 'e2-e4');
