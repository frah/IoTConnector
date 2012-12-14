CREATE DATABASE IF NOT EXISTS `iotc` DEFAULT CHARACTER SET utf8;

USE `iotc`;

DROP TABLE IF EXISTS `log`;
DROP TABLE IF EXISTS `power`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `ref_task_command`;
DROP TABLE IF EXISTS `task`;
DROP TABLE IF EXISTS `ref_term_method`;
DROP TABLE IF EXISTS `ref_term_sensor`;
DROP TABLE IF EXISTS `term`;
DROP TABLE IF EXISTS `sensor_value`;
DROP TABLE IF EXISTS `statistic_value`;
DROP TABLE IF EXISTS `ref_method_sensor`;
DROP TABLE IF EXISTS `statistical_method`;
DROP TABLE IF EXISTS `sensor`;
DROP TABLE IF EXISTS `sensor_type`;
DROP TABLE IF EXISTS `command`;
DROP TABLE IF EXISTS `device`;
DROP TABLE IF EXISTS `room`;


/*
<ユーザテーブル> ユーザ情報を格納
id          : ユーザID
name        : ユーザ名
alias_name  : 各インフラにおけるユーザ名の定義
*/
CREATE TABLE `user` (
    `id` integer unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `alias_name` text,
    PRIMARY KEY (`id`),
    KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8;

CREATE TABLE `power` (
    `user_id` integer unsigned  NOT NULL,
    `power` integer(2) NOT NULL DEFAULT '0',
    `prev_power` integer(2) DEFAULT '0',
    `type` integer(1) NOT NULL DEFAULT '0',
    `period` integer DEFAULT NULL,
    `auth_by` integer unsigned DEFAULT NULL,
    PRIMARY KEY (`user_id`),
    FOREIGN KEY (`user_id`) REFERENCES user (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`auth_by`) REFERENCES user (`id`) ON UPDATE SET NULL
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8;

CREATE TABLE `room` (
    `id` integer unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `explanation` varchar(255) DEFAULT '',
    PRIMARY KEY (`id`),
    KEY `name` (`name`),
    UNIQUE (`name`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8;

CREATE TABLE `device` (
    `id` integer unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `room_id` integer unsigned NOT NULL,
    `UDN` varchar(255) NOT NULL,
    `type` integer(1) NOT NULL,
    `explanation` varchar(255) DEFAULT '',
    PRIMARY KEY (`id`),
    KEY `name` (`name`),
    KEY `UDN` (`UDN`),
    FOREIGN KEY (`room_id`) REFERENCES room (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8;

CREATE TABLE `command` (
    `id` integer unsigned NOT NULL AUTO_INCREMENT,
    `device_id` integer unsigned NOT NULL,
    `name` varchar(255) NOT NULL,
    `type` integer(1) NOT NULL,
    `power` integer(2) NOT NULL,
    `command` text NOT NULL,
    `alias_name` varchar(255),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`device_id`) REFERENCES device (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8;

CREATE TABLE `log` (
    `id` integer unsigned NOT NULL AUTO_INCREMENT,
    `user_id` integer unsigned NOT NULL,
    `com_id` integer unsigned,
    `com_variable` text,
    `state` integer(1) NOT NULL,
    `relational_log` integer unsigned,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES user (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`com_id`) REFERENCES command (`id`) ON DELETE SET NULL,
    FOREIGN KEY (`relational_log`) REFERENCES log (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8;

CREATE TABLE `sensor_type` (
    `id` integer unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `unit` varchar(32) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8;

CREATE TABLE `sensor` (
    `id` integer unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `type` integer unsigned NOT NULL,
    `device_id` integer unsigned NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`type`) REFERENCES sensor_type (`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`device_id`) REFERENCES device (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8;

CREATE TABLE `sensor_value` (
    `id` integer unsigned NOT NULL AUTO_INCREMENT,
    `sensor` integer unsigned NOT NULL,
    `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `value` double NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`sensor`) REFERENCES sensor (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8;

CREATE TABLE `statistical_method` (
    `id` integer unsigned NOT NULL AUTO_INCREMENT,
    `method` text NOT NULL,
    `timespan` integer NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8;

CREATE TABLE `ref_method_sensor` (
    `method` integer unsigned NOT NULL,
    `sensor` integer unsigned NOT NULL,
    FOREIGN KEY (`method`) REFERENCES statistical_method (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`sensor`) REFERENCES sensor (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8;

CREATE TABLE `statistic_value` (
    `id` integer unsigned NOT NULL AUTO_INCREMENT,
    `method` integer unsigned NOT NULL,
    `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `value` double NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`method`) REFERENCES statistical_method (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8;

CREATE TABLE `term` (
    `id` integer unsigned NOT NULL AUTO_INCREMENT,
    `term` text NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8;

CREATE TABLE `ref_term_sensor` (
    `term` integer unsigned NOT NULL,
    `sensor` integer unsigned NOT NULL,
    FOREIGN KEY (`term`) REFERENCES term (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`sensor`) REFERENCES sensor (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8;

CREATE TABLE `ref_term_method` (
    `term` integer unsigned NOT NULL,
    `method` integer unsigned NOT NULL,
    FOREIGN KEY (`term`) REFERENCES term (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`method`) REFERENCES statistical_method (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8;

CREATE TABLE `task` (
    `id` integer unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `term` integer unsigned NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`term`) REFERENCES term (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8;

CREATE TABLE `ref_task_command` (
    `task` integer unsigned NOT NULL,
    `command` integer unsigned NOT NULL,
    FOREIGN KEY (`task`) REFERENCES task (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`command`) REFERENCES command (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8;
