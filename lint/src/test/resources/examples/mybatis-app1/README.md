```sql-- Adminer 4.8.1 MySQL 8.0.28 dump

SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

SET NAMES utf8mb4;

DROP DATABASE IF EXISTS `public`;
CREATE DATABASE `public` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `public`;

DROP TABLE IF EXISTS `Author`;
CREATE TABLE `Author` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `Blog`;
CREATE TABLE `Blog` (
  `id` int NOT NULL AUTO_INCREMENT,
  `author_id` int NOT NULL,
  `blog_name` varchar(100) NOT NULL,
  `description` varchar(100) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `author_id` (`author_id`),
  CONSTRAINT `Blog_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `Author` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `Content`;
CREATE TABLE `Content` (
  `id` int NOT NULL AUTO_INCREMENT,
  `author_id` int NOT NULL,
  `blog_id` int NOT NULL,
  `content` varchar(100) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `blog_id` (`blog_id`),
  KEY `author_id` (`author_id`),
  CONSTRAINT `Content_ibfk_1` FOREIGN KEY (`blog_id`) REFERENCES `Blog` (`id`),
  CONSTRAINT `Content_ibfk_2` FOREIGN KEY (`author_id`) REFERENCES `Author` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

```