```sql
-- Adminer 4.8.1 MySQL 8.0.28 dump

SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

SET NAMES utf8mb4;

CREATE DATABASE `public` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `public`;

DROP TABLE IF EXISTS `Blog`;
CREATE TABLE `Blog` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `category` tinyint NOT NULL,
  `owner` varchar(100) NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `Content`;
CREATE TABLE `Content` (
  `text` varchar(1000) NOT NULL,
  `blog_id` int NOT NULL,
  `title` varchar(100) NOT NULL,
  `writer` varchar(100) NOT NULL,
  `id` tinyint NOT NULL AUTO_INCREMENT,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `writer` (`writer`),
  KEY `blog_id` (`blog_id`),
  CONSTRAINT `Content_ibfk_2` FOREIGN KEY (`writer`) REFERENCES `Writer` (`email`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `Content_ibfk_3` FOREIGN KEY (`blog_id`) REFERENCES `Blog` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `Writer`;
CREATE TABLE `Writer` (
  `location` varchar(100) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `age` int DEFAULT NULL,
  `company` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 2022-03-30 02:17:12
```