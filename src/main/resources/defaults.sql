CREATE TABLE IF NOT EXISTS ows_history_entries (
    message_id BIGINT PRIMARY KEY NOT NULL,
    author_id BIGINT NOT NULL,
    content VARCHAR(255) NOT NULL
);