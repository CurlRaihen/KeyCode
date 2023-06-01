-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: localhost    Database: verwaltungsabteilung
-- ------------------------------------------------------
-- Server version	8.0.31

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `verwaltungsabteilung`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `verwaltungsabteilung` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `verwaltungsabteilung`;

--
-- Table structure for table `bereiche`
--

DROP TABLE IF EXISTS `bereiche`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bereiche` (
  `bereich_id` bigint NOT NULL AUTO_INCREMENT,
  `bereichsname` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  PRIMARY KEY (`bereich_id`),
  UNIQUE KEY `bereichsname` (`bereichsname`),
  KEY `idx_v_bereichsname` (`bereichsname`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bereiche`
--

LOCK TABLES `bereiche` WRITE;
/*!40000 ALTER TABLE `bereiche` DISABLE KEYS */;
INSERT INTO bereiche(bereich_id, bereichsname) VALUES
(1, 'Seminarräume FIM'),
(2, 'Seminarräume ITZ'),
(3, 'Seminarräume WIWI'),
(4, 'Pc-Pool WIWI'),
(5, 'Lehrstuhl Fraser'),
(6, 'Lehrstuhl Lehner'),
(7, 'Lehrstuhl Totzek'),
(8, 'Lehrstuhl Beurskens'), 
(9, 'Hörsäle Audimax'),
(10, 'Hörsäle Philo'),
(11, 'Hallen Sportzentrum');

/*!40000 ALTER TABLE `bereiche` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `besteht_aus`
--

DROP TABLE IF EXISTS `besteht_aus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `besteht_aus` (
  `bereich_id` bigint NOT NULL,
  `raum_id` bigint NOT NULL,
  PRIMARY KEY (`bereich_id`,`raum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `besteht_aus`
--

LOCK TABLES `besteht_aus` WRITE;
/*!40000 ALTER TABLE `besteht_aus` DISABLE KEYS */;
INSERT INTO besteht_aus(bereich_id, raum_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(1, 7),
(2, 8),
(2, 9),
(2, 10),
(2, 11),
(2, 12),
(3, 13),
(3, 14),
(3, 15),
(3, 16),
(3, 17),
(3, 18),
(4, 19),
(4, 20),
(4, 21),
(5, 22),
(5, 23),
(5, 24),
(5, 25),
(6, 26),
(6, 27),
(6, 28),
(6, 29),
(6, 30),
(7, 31),
(7, 32),
(7, 33),
(7, 34),
(8, 35),
(8, 36),
(8, 37),
(8, 38),
(9, 39),
(9, 40),
(9, 41),
(9, 42),
(10, 43),
(10, 44),
(10, 45),
(10, 46),
(11, 47),
(11, 48);

/*!40000 ALTER TABLE `besteht_aus` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `räume`
--

DROP TABLE IF EXISTS `räume`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `räume` (
  `raum_id` bigint NOT NULL AUTO_INCREMENT,
  `gebäudename` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `raumname` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `ist_sensitiv` enum('Ja','Nein') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'Nein',
  PRIMARY KEY (`raum_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `räume`
--

LOCK TABLES `räume` WRITE;
/*!40000 ALTER TABLE `räume` DISABLE KEYS */;
INSERT INTO räume(raum_id, gebäudename, raumname, ist_sensitiv) VALUES
(1, 'FIM', 'IM-SR004', 'Nein'),
(2, 'FIM', 'IM-SR007', 'Nein'),
(3, 'FIM', 'IM-SR008b', 'Nein'),
(4, 'FIM', 'IM-SR010', 'Nein'),
(5, 'FIM', 'IM-SR030', 'Nein'),
(6, 'FIM', 'IM-SR033', 'Nein'),
(7, 'FIM', 'IM-SR034', 'Nein'),
(8, 'ITZ', 'ITZ-SR001', 'Nein'),
(9, 'ITZ', 'ITZ-SR002', 'Nein'),
(10, 'ITZ', 'ITZ-SR004', 'Nein'),
(11, 'ITZ', 'ITZ-SR011', 'Nein'),
(12, 'ITZ', 'ITZ-SR252', 'Nein'),
(13, 'WIWI', 'WIWI-SR026', 'Nein'),
(14, 'WIWI', 'WIWI-SR027', 'Nein'),
(15, 'WIWI', 'WIWI-SR028', 'Nein'),
(16, 'WIWI', 'WIWI-SR029', 'Nein'),
(17, 'WIWI', 'WIWI-SR033', 'Nein'),
(18, 'WIWI', 'WIWI-SR034', 'Nein'),
(19, 'WIWI', 'WIWI-PC030a', 'Nein'),
(20, 'WIWI', 'WIWI-PC031', 'Nein'),
(21, 'WIWI', 'WIWI-PC032', 'Nein'),
(22, 'FIM', 'IM-131', 'Ja'),
(23, 'FIM', 'IM-216', 'Nein'),
(24, 'FIM', 'IM-109', 'Nein'),
(25, 'FIM', 'IM-110', 'Nein'),
(26, 'ITZ', 'ITZ-262', 'Ja'),
(27, 'ITZ', 'ITZ-261', 'Nein'),
(28, 'ITZ', 'ITZ-258', 'Nein'),
(29, 'ITZ', 'ITZ-256', 'Nein'),
(30, 'ITZ', 'ITZ-260', 'Nein'),
(31, 'WIWI', 'WIWI-115', 'Ja'),
(32, 'WIWI', 'WIWI-116', 'Nein'),
(33, 'WIWI', 'WIWI-117', 'Nein'),
(34, 'WIWI', 'WIWI-119', 'Nein'),
(35, 'Juridicum', 'JUR-215', 'Ja'),
(36, 'Juridicum', 'JUR-214', 'Nein'),
(37, 'Juridicum', 'JUR-207', 'Nein'),
(38, 'Juridicum', 'JUR-206', 'Nein'),
(39, 'Audimax', 'AM-HS9', 'Nein'),
(40, 'Audimax', 'AM-HS10', 'Ja'),
(41, 'Audimax', 'AM-HS12', 'Nein'),
(42, 'Audimax', 'AM-HS13', 'Nein'),
(43, 'Philosophicum', 'Philo-HS1', 'Nein'),
(44, 'Philosophicum', 'Philo-HS2', 'Nein'),
(45, 'Philosophicum', 'Philo-HS3', 'Nein'),
(46, 'Philosophicum', 'Philo-HS4', 'Nein'),
(47, 'Sportzentrum', 'SP-SH066', 'Nein'),
(48, 'Sportzentrum', 'SP-MZH070', 'Ja');
/*!40000 ALTER TABLE `räume` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `türen`
--

DROP TABLE IF EXISTS `türen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `türen` (
  `tür_id` bigint NOT NULL AUTO_INCREMENT,
  `raum_id` bigint NOT NULL,
  PRIMARY KEY (`tür_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `türen`
--

LOCK TABLES `türen` WRITE;
/*!40000 ALTER TABLE `türen` DISABLE KEYS */;
INSERT INTO türen(tür_id, raum_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 7),
(8, 8),
(9, 9),
(10, 10),
(11, 11),
(12, 12),
(13, 13),
(14, 14),
(15, 15),
(16, 16),
(17, 17),
(18, 18),
(19, 19),
(20, 20),
(21, 21),
(22, 22),
(23, 23),
(24, 24),
(25, 25),
(26, 26),
(27, 27),
(28, 28),
(29, 29),
(30, 30),
(31, 31),
(32, 32),
(33, 33),
(34, 34),
(35, 35),
(36, 36),
(37, 37),
(38, 38),
(39, 39),
(40, 40),
(41, 41),
(42, 42),
(43, 43),
(44, 44),
(45, 45),
(46, 46),
(47, 47),
(48, 47),
(49, 48),
(50, 48);
/*!40000 ALTER TABLE `türen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `key_management`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `key_management` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `key_management`;

--
-- Table structure for table `anträge`
--

DROP TABLE IF EXISTS `anträge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `anträge` (
  `antrag_id` bigint NOT NULL,
  `benutzername` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `bearbeitungsstelle` enum('Verwaltungsmitarbeiter','Verwaltungsleitung') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'Verwaltungsmitarbeiter',
  `erstellungszeitpunkt` timestamp NULL DEFAULT NULL,
  `status` enum('Nicht_bearbeitet','In_Bearbeitung','Angenommen','Abgelehnt') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'Nicht_bearbeitet',
  `datei` mediumblob,
  `antragsart` enum('NEU','BESTEHEND') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `keycard_id` int DEFAULT NULL,
  `kommentar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `ist_sensitiv` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `bearbeiter` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `anfangstag` date DEFAULT NULL,
  `endtag` date DEFAULT NULL,
  PRIMARY KEY (`antrag_id`),
  KEY `idx_k_antrag_benutzername` (`benutzername`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `anträge`
--

LOCK TABLES `anträge` WRITE;
/*!40000 ALTER TABLE `anträge` DISABLE KEYS */;
/*!40000 ALTER TABLE `anträge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `archiv_akteure`
--

DROP TABLE IF EXISTS `archiv_akteure`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `archiv_akteure` (
  `konto_id` bigint NOT NULL,
  `benutzername` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `passwort` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `rolle` enum('Admin','Verwaltungsleitung','Verwaltungsmitarbeiter','Nutzer') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `erstellungszeitpunkt` date NOT NULL,
  `vorname` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `nachname` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `geburtsdatum` date DEFAULT NULL,
  `telefonnummer` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `email` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `adresse` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `arbeitsvertragsende` date DEFAULT NULL,
  `erlaubt` tinyint DEFAULT NULL,
  PRIMARY KEY (`konto_id`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  UNIQUE KEY `telefonnummer_UNIQUE` (`telefonnummer`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archiv_akteure`
--

LOCK TABLES `archiv_akteure` WRITE;
/*!40000 ALTER TABLE `archiv_akteure` DISABLE KEYS */;
/*!40000 ALTER TABLE `archiv_akteure` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `archiv_anträge`
--

DROP TABLE IF EXISTS `archiv_anträge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `archiv_anträge` (
  `antrag_id` int NOT NULL,
  `benutzername` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `keycard_id` int DEFAULT NULL,
  `bearbeitungsstelle` enum('Verwaltungsmitarbeiter','Verwaltungsleitung') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `bearbeitername` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `erstellungszeitpunkt` timestamp NULL DEFAULT NULL,
  `status` enum('Nicht_bearbeitet','Angenommen','Abgelehnt') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `datei` mediumblob,
  `anfangstag` date DEFAULT NULL,
  `endtag` date DEFAULT NULL,
  `bereiche` varchar(45) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `raeume` varchar(45) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  PRIMARY KEY (`antrag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archiv_anträge`
--

LOCK TABLES `archiv_anträge` WRITE;
/*!40000 ALTER TABLE `archiv_anträge` DISABLE KEYS */;
/*!40000 ALTER TABLE `archiv_anträge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `beantragt_für`
--

DROP TABLE IF EXISTS `beantragt_für`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `beantragt_für` (
  `beantragt_id` bigint NOT NULL,
  `antrag_id` bigint NOT NULL,
  `raum_id` bigint,
  `bereich_id` bigint,
  PRIMARY KEY (`beantragt_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `beantragt_für`
--

LOCK TABLES `beantragt_für` WRITE;
/*!40000 ALTER TABLE `beantragt_für` DISABLE KEYS */;
/*!40000 ALTER TABLE `beantragt_für` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hat_zugang`
--

DROP TABLE IF EXISTS `hat_zugang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hat_zugang` (
  `zugang_id`bigint NOT NULL,
  `keycard_id` bigint NOT NULL,
  `raum_id` bigint,
  `bereich_id` bigint,
  PRIMARY KEY (`zugang_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hat_zugang`
--

LOCK TABLES `hat_zugang` WRITE;
/*!40000 ALTER TABLE `hat_zugang` DISABLE KEYS */;
INSERT INTO `hat_zugang` VALUES
(1,1, NULL, 4),
(2,1, 1, NULL);
/*!40000 ALTER TABLE `hat_zugang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `keycards`
--

DROP TABLE IF EXISTS `keycards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `keycards` (
  `keycard_id` int NOT NULL AUTO_INCREMENT,
  `benutzername` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `anfangstag` date NOT NULL,
  `endtag` date NOT NULL,
  `status` enum('Aktiviert','Deaktiviert','Gesperrt') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  PRIMARY KEY (`keycard_id`),
  KEY `idx_k_keycard_benutzername` (`benutzername`)
) ENGINE=InnoDB AUTO_INCREMENT=9223372036854775807 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `keycards`
--

LOCK TABLES `keycards` WRITE;
/*!40000 ALTER TABLE `keycards` DISABLE KEYS */;
INSERT INTO `keycards` VALUES
(1,'Moritz','2023-01-01','2023-12-31','Aktiviert');
/*!40000 ALTER TABLE `keycards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log_keycards`
--

DROP TABLE IF EXISTS `log_keycards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `log_keycards` (
  `log_id` int NOT NULL,
  `keycard_id` int DEFAULT NULL,
  `bearbeitername` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `typ` enum('Aktivierung','Deaktivierung','Erstellung','Sperrung','Rechte_Änderung') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `erstellungszeitpunkt` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `log_keycards`
--

LOCK TABLES `log_keycards` WRITE;
/*!40000 ALTER TABLE `log_keycards` DISABLE KEYS */;
INSERT INTO `log_keycards` VALUES
(1, 1, 'Moritz', 'Erstellung', '2023-01-01 00:00:00');
/*!40000 ALTER TABLE `log_keycards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log_türen`
--

DROP TABLE IF EXISTS `log_türen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `log_türen` (
  `log_id` bigint NOT NULL,
  `keycard_id` bigint DEFAULT NULL,
  `tür_id` bigint DEFAULT NULL,
  `ergebnis` enum('Akzeptiert','Nicht_akzeptiert') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `erstellungszeitpunkt` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `log_türen`
--

LOCK TABLES `log_türen` WRITE;
/*!40000 ALTER TABLE `log_türen` DISABLE KEYS */;
/*!40000 ALTER TABLE `log_türen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `personalabteilung`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `personalabteilung` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `personalabteilung`;

--
-- Table structure for table `konten`
--

DROP TABLE IF EXISTS `konten`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `konten` (
  `konto_id` bigint NOT NULL AUTO_INCREMENT,
  `benutzername` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `passwort` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `rolle` enum('Administrator','Verwaltungsleitung','Verwaltungsmitarbeiter','Nutzer') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `erstellungszeitpunkt` date NOT NULL,
  `vorname` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `nachname` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `geburtsdatum` date DEFAULT NULL,
  `telefonnummer` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `email` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `adresse` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `arbeitsvertragsende` date DEFAULT NULL,
  `erlaubt` tinyint DEFAULT NULL,
  PRIMARY KEY (`konto_id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `telefonnummer` (`telefonnummer`),
  KEY `idx_p_benutzername` (`konto_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `konten`
--

LOCK TABLES `konten` WRITE;
/*!40000 ALTER TABLE `konten` DISABLE KEYS */;
INSERT INTO `konten` VALUES 
(1,'Franz','$2a$12$LOWJ.C2I8hAf7vojmjCr1.OAau1VvjCwuSi4AOXjbGyQaZWYAshqy','Nutzer','2023-01-01','Franz','Lehner','2003-03-03','+49(0)851/509-2590','Franz.Lehner@uni-passau.de','ITZ-262','2999-12-31',1),
(2,'Tobias','$2a$12$xKRAy2G1QkQDBdJtwXOK3e1RFt8q..mz02QHMpycQunr9u4yoGR4a','Nutzer','2023-01-01','Tobias','Baumgärtner','2003-03-03','+49(0)851/509-2594','Tobias.Baumgärtner@uni-passau.de','ITZ-258','2025-12-31',1),
(3,'Thomas','$2a$12$nRZXz8X8eTONf2BrElN4feb2DpGMGnAH2riQ3g5X7JhwYiWFNFJ4W','Nutzer','2023-01-01','Thomas','Fritsch','2003-03-03','+49(0)851/509-2599','Thomas.Fritsch@uni-passau.de','ITZ-258','2025-12-31',1),
(4,'Tri','$2a$12$8Ohi3zSMmLbRnEHbBqetwOKciAOrAa2WMM6ifNWFttQwOb45GtqLe','Verwaltungsmitarbeiter','2023-01-01','Tri','Kieu','1998-03-03','+49(0)851/509-2696','kieu01@ads.uni-passau.de','WIWI-116','2023-12-31',1),
(5,'Henrik','$2a$12$VBn/KATpEbxStFuxTya2AORrldv8rMtf1xzdX.0/d1joSFAevceJm','Verwaltungsmitarbeiter','2023-01-01','Henrik','Raithle','2000-04-04','+49(0)851/509-2697','raithl02@ads.uni-passau.de','WIWI-116','2023-12-31',1),
(6,'Jannik','$2a$12$2i1xewko27.QeOymt9JLBu85pizmNleK68mEFUaeTWOw7SdzTiF4u','Verwaltungsmitarbeiter','2023-01-01','Jannik','Segerer','2001-05-05','+49(0)851/509-2698','segere03@ads.uni-passau.de','WIWI-116','2023-12-31',1),
(7,'Moritz','$2a$12$.H4MY0QYjSbRVDlwJmKyieqeGEzhTqzjBYUQhLA4ynInZcejjqcS2','Verwaltungsleitung','2023-01-01','Moritz','Grimm','1999-02-02','+49(0)851/509-2600','grimm28@ads.uni-passau.de','WIWI-116','2023-12-31',1),
(8,'Maxi','$2a$12$9SZwLp.iCWUnaR/MoPN5OeoUH6mzvPi0Q33MZkOmvZd3cgb.mCU.q','Administrator','2023-01-01','Maximilian','Rösch','2002-06-06','+49(0)851/509-2601','roesch15@ads.uni-passau.de','WIWI-116','2023-12-31',1);
/*!40000 ALTER TABLE `konten` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-01-08 16:59:52
