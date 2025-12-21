use mynews_users;

CREATE TABLE user_preferences (
	`id` VARCHAR(40),
    `user_email` VARCHAR(500) NOT NULL,
    `categories` JSON NOT NULL,
    `sources` JSON DEFAULT NULL,
	`created_at` DATETIME DEFAULT current_timestamp() NOT NULL,
	`created_by` VARCHAR(100) DEFAULT 'system' NOT NULL,
	`modified_at` DATETIME DEFAULT current_timestamp() NOT NULL,
	`modified_by` VARCHAR(100) DEFAULT NULL,
	`rowversion` INT default 1 NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_USR_EMAIL` (`user_email`)
)ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

