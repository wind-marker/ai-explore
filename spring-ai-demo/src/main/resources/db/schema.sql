CREATE TABLE IF NOT EXISTS SPRING_AI_CHAT_MEMORY (
    `conversation_id` VARCHAR(36) NOT NULL,
    `content` TEXT NOT NULL,
    `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `type` varchar(10) not null,
    INDEX CONVERSATION_ID_TIMESTAMP_IDX(`conversation_id`, `timestamp`)
    );
