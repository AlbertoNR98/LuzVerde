-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: luzverde
-- ------------------------------------------------------
-- Server version	8.0.19

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
-- Table structure for table `cruce`
--

DROP TABLE IF EXISTS `cruce`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cruce` (
  `idCruce` int NOT NULL,
  `ipCruce` varchar(45) DEFAULT NULL,
  `nombreCruce` varchar(45) DEFAULT NULL,
  `initialTimestamp` bigint DEFAULT NULL,
  `idUsuario` int NOT NULL,
  PRIMARY KEY (`idCruce`),
  UNIQUE KEY `idCruce_UNIQUE` (`idCruce`),
  KEY `cruce_usuario_idx` (`idUsuario`),
  CONSTRAINT `cruce_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cruce`
--

LOCK TABLES `cruce` WRITE;
/*!40000 ALTER TABLE `cruce` DISABLE KEYS */;
INSERT INTO `cruce` VALUES (1,'100.100.100.100','Avda. Palmera - Luca de Tena',111111111,1),(2,'100.100.100.101','Avda. Palmera - Cardenal Ilundain',111111112,2);
/*!40000 ALTER TABLE `cruce` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `luz_semaforo`
--

DROP TABLE IF EXISTS `luz_semaforo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `luz_semaforo` (
  `idLuz_Semaforo` int NOT NULL AUTO_INCREMENT,
  `color` varchar(45) DEFAULT NULL,
  `timestamp` bigint DEFAULT NULL,
  `idSemaforo` int DEFAULT NULL,
  PRIMARY KEY (`idLuz_Semaforo`),
  KEY `luz_semaforo_semaforo_idx` (`idSemaforo`),
  CONSTRAINT `luz_semaforo_semaforo` FOREIGN KEY (`idSemaforo`) REFERENCES `semaforo` (`idSemaforo`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `luz_semaforo`
--

LOCK TABLES `luz_semaforo` WRITE;
/*!40000 ALTER TABLE `luz_semaforo` DISABLE KEYS */;
INSERT INTO `luz_semaforo` VALUES (1,'Verde',100000,1),(2,'Verde',100000,2),(3,'Rojo',100000,3),(4,'Rojo',100000,4),(5,'Verde',100300,5),(6,'Verde',100300,6),(7,'Rojo',100300,7),(8,'Rojo',100300,8),(9,'Rojo',110000,1),(10,'Rojo',110000,2),(11,'Verde',110000,3),(12,'Verde',110000,4),(13,'Rojo',110300,5),(14,'Rojo',110300,6),(15,'Verde',110300,7),(16,'Verde',110300,8);
/*!40000 ALTER TABLE `luz_semaforo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `semaforo`
--

DROP TABLE IF EXISTS `semaforo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `semaforo` (
  `idSemaforo` int NOT NULL AUTO_INCREMENT,
  `idCruce` int NOT NULL,
  `nombreSemaforo` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idSemaforo`),
  UNIQUE KEY `idSemaforo_UNIQUE` (`idSemaforo`),
  KEY `semaforo_cruce_idx` (`idCruce`),
  CONSTRAINT `semaforo_cruce` FOREIGN KEY (`idCruce`) REFERENCES `cruce` (`idCruce`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `semaforo`
--

LOCK TABLES `semaforo` WRITE;
/*!40000 ALTER TABLE `semaforo` DISABLE KEYS */;
INSERT INTO `semaforo` VALUES (1,1,'AVP1'),(2,1,'AVP2'),(3,1,'MS1'),(4,1,'MS2'),(5,2,'AVP1'),(6,2,'AVP2'),(7,2,'CI1'),(8,2,'CI2');
/*!40000 ALTER TABLE `semaforo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sensor`
--

DROP TABLE IF EXISTS `sensor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sensor` (
  `idSensor` int NOT NULL AUTO_INCREMENT,
  `tipoSensor` varchar(45) NOT NULL,
  `nombreSensor` varchar(45) NOT NULL,
  `idSemaforo` int DEFAULT NULL,
  PRIMARY KEY (`idSensor`),
  KEY `sensor_semaforo_idx` (`idSemaforo`),
  CONSTRAINT `sensor_semaforo` FOREIGN KEY (`idSemaforo`) REFERENCES `semaforo` (`idSemaforo`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sensor`
--

LOCK TABLES `sensor` WRITE;
/*!40000 ALTER TABLE `sensor` DISABLE KEYS */;
INSERT INTO `sensor` VALUES (1,'TempHum','TempHum1',1),(3,'CO2','ContAire1',1),(4,'Ruido','ContAcust1',1),(5,'CO2','ContAire2',2),(6,'Ruido','ContAcust2',2),(7,'CO2','ContAire3',3),(8,'Ruido','ContAcust3',3),(9,'CO2','ContAire4',4),(10,'Ruido','ContAcust4',4),(11,'TempHum','TempHum2',5),(13,'CO2','ContAire5',5),(14,'Ruido','ContAcust5',5),(15,'CO2','ContAire6',6),(16,'Ruido','ContAcust6',6),(17,'CO2','ContAire7',7),(18,'Ruido','ContAcust7',7),(19,'CO2','ContAire8',8),(20,'Ruido','ContAcust8',8);
/*!40000 ALTER TABLE `sensor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `idUsuario` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) NOT NULL,
  `apellidos` varchar(45) DEFAULT NULL,
  `dni` varchar(45) DEFAULT NULL,
  `fnacimiento` bigint DEFAULT NULL,
  PRIMARY KEY (`idUsuario`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'Alberto','Naranjo','12345698X',161098),(2,'José Joaquín','Comitre','9876541',181099);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `valor_sensor_contaminacion`
--

DROP TABLE IF EXISTS `valor_sensor_contaminacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `valor_sensor_contaminacion` (
  `idValor_sensor_contaminacion` int NOT NULL AUTO_INCREMENT,
  `value` float NOT NULL,
  `accuracy` float NOT NULL,
  `timestamp` bigint DEFAULT NULL,
  `idSensor` int NOT NULL,
  PRIMARY KEY (`idValor_sensor_contaminacion`),
  KEY `valor_sensor_contaminacion_sensor_idx` (`idSensor`),
  CONSTRAINT `valor_sensor_contaminacion_sensor` FOREIGN KEY (`idSensor`) REFERENCES `sensor` (`idSensor`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `valor_sensor_contaminacion`
--

LOCK TABLES `valor_sensor_contaminacion` WRITE;
/*!40000 ALTER TABLE `valor_sensor_contaminacion` DISABLE KEYS */;
INSERT INTO `valor_sensor_contaminacion` VALUES (1,1.6,1,99999999,3),(2,1.1,1,99999999,4),(3,1.8,1,99999999,5),(4,1.9,1,99999999,6),(5,2.2,1,99999999,7),(6,3.2,1,99999999,8),(7,1.1,1,99999999,9),(8,2.2,1,99999999,10),(9,1.15,1,99999999,13),(10,2.05,1,99999999,14),(11,3.02,1,99999999,15),(12,2.33,1,99999999,16),(13,2.44,1,99999999,17),(14,3.05,1,99999999,18),(15,0.95,1,99999999,19),(16,2.69,1,99999999,20);
/*!40000 ALTER TABLE `valor_sensor_contaminacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `valor_sensor_temp_hum`
--

DROP TABLE IF EXISTS `valor_sensor_temp_hum`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `valor_sensor_temp_hum` (
  `idValor_sensor_temp_hum` int NOT NULL AUTO_INCREMENT,
  `valueTemp` float NOT NULL,
  `accuracyTemp` float NOT NULL,
  `valueHum` float NOT NULL,
  `accuracyHum` float NOT NULL,
  `timestamp` bigint DEFAULT NULL,
  `idSensor` int NOT NULL,
  PRIMARY KEY (`idValor_sensor_temp_hum`),
  KEY `valor_sensor_temp_hum_sensor_idx` (`idSensor`),
  CONSTRAINT `valor_sensor_temp_hum_sensor` FOREIGN KEY (`idSensor`) REFERENCES `sensor` (`idSensor`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `valor_sensor_temp_hum`
--

LOCK TABLES `valor_sensor_temp_hum` WRITE;
/*!40000 ALTER TABLE `valor_sensor_temp_hum` DISABLE KEYS */;
INSERT INTO `valor_sensor_temp_hum` VALUES (1,30,2,50,5,33333333,1),(2,31,2,40,3,33333333,11);
/*!40000 ALTER TABLE `valor_sensor_temp_hum` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-03-27 19:44:38
