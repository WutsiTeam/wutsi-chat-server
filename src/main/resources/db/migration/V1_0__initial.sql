CREATE TABLE T_MESSAGE(
    id                          SERIAL NOT NULL,
    tenant_id                   BIGINT NOT NULL,
    sender_id                   INT NOT NULL,
    recipient_id                INT NOT NULL,
    conversation_id             VARCHAR(32),
    device_id                   VARCHAR(36),
    text                        TEXT,
    created                     TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY(id)
);

CREATE INDEX I_MESSAGE_conversation_id_created ON T_MESSAGE(conversation_id, created);
