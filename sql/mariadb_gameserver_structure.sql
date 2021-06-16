-- phpMyAdmin SQL Dump
-- version 4.9.5deb2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jun 09, 2021 at 04:40 PM
-- Server version: 8.0.25-0ubuntu0.20.04.1
-- PHP Version: 7.4.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

-- ----------------------------------------------------------------------------------------------------------------
-- Database: `gameserver`
--
DROP DATABASE IF EXISTS `gameserver`;
CREATE DATABASE IF NOT EXISTS `gameserver` DEFAULT CHARACTER SET latin1 COLLATE latin1_general_cs;
USE `gameserver`;

-- ----------------------------------------------------------------------------------------------------------------
-- Table structure for table `ports`
--
CREATE TABLE IF NOT EXISTS `ports` (
  `public_ip` varchar(15) COLLATE latin1_general_cs NOT NULL,
  `used` longtext NOT NULL,
  `available` longtext NOT NULL,
  PRIMARY KEY (`public_ip`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

-- ----------------------------------------------------------------------------------------------------------------
-- Table structure for table `servers`
--
CREATE TABLE IF NOT EXISTS `servers` (
  `server_id` int UNSIGNED NOT NULL,
  `ip` varchar(15) COLLATE latin1_general_cs NOT NULL,
  `port` smallint UNSIGNED NOT NULL,
  `name` varchar(30) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `description` varchar(200) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  `game` varchar(30) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `game_version` varchar(10) COLLATE latin1_general_cs NOT NULL,
  `n_max_players` smallint UNSIGNED NOT NULL,
  `opened_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ready_for_shutdown` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`server_id`),
  UNIQUE KEY `port` (`port`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

-- ----------------------------------------------------------------------------------------------------------------
-- Table structure for table `settings`
--
CREATE TABLE IF NOT EXISTS `settings` (
  `setting_key` varchar(20) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `setting_value` varchar(100) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  UNIQUE KEY `setting_key` (`setting_key`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

COMMIT;

