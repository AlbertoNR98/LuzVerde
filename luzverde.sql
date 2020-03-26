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
  `cruceID` int NOT NULL AUTO_INCREMENT,
  `nombreCruce` varchar(45) DEFAULT NULL,
  `longitud` float NOT NULL,
  `latitud` float NOT NULL,
  `UsuarioID` int DEFAULT NULL,
  PRIMARY KEY (`cruceID`),
  KEY `cruce_usuario_idx` (`UsuarioID`),
  CONSTRAINT `cruce_usuario` FOREIGN KEY (`UsuarioID`) REFERENCES `usuario` (`idUsuario`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cruce`
--

LOCK TABLES `cruce` WRITE;
/*!40000 ALTER TABLE `cruce` DISABLE KEYS */;
/*!40000 ALTER TABLE `cruce` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `luz-semaforo`
--

DROP TABLE IF EXISTS `luz-semaforo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `luz-semaforo` (
  `luzSemaforoID` int NOT NULL AUTO_INCREMENT,
  `ColorLuz` varchar(45) NOT NULL,
  `TimeStamp` bigint DEFAULT NULL,
  `SemaforoID` int DEFAULT NULL,
  PRIMARY KEY (`luzSemaforoID`),
  KEY `SemaforoID_idx` (`SemaforoID`),
  CONSTRAINT `SemaforoID` FOREIGN KEY (`SemaforoID`) REFERENCES `semaforo` (`idSemaforo`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `luz-semaforo`
--

LOCK TABLES `luz-semaforo` WRITE;
/*!40000 ALTER TABLE `luz-semaforo` DISABLE KEYS */;
/*!40000 ALTER TABLE `luz-semaforo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `semaforo`
--

DROP TABLE IF EXISTS `semaforo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `semaforo` (
  `idSemaforo` int NOT NULL AUTO_INCREMENT COMMENT 'Deberia venir marcado por el propio ID del dispositivo y no autoincremental\\n',
  `ip` varchar(45) DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `initialTimeStamp` bigint DEFAULT NULL,
  `CruceID` int DEFAULT NULL,
  PRIMARY KEY (`idSemaforo`),
  KEY `semaforo_cruce_idx` (`CruceID`),
  CONSTRAINT `semaforo_cruce` FOREIGN KEY (`CruceID`) REFERENCES `cruce` (`cruceID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Dispositivo en el que van los sensores/actuadores';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `semaforo`
--

LOCK TABLES `semaforo` WRITE;
/*!40000 ALTER TABLE `semaforo` DISABLE KEYS */;
INSERT INTO `semaforo` VALUES (1,'192.168.0.108','Casa',123456456436,NULL),(2,'150.215.489.215','Trabajo',123841234738,NULL);
/*!40000 ALTER TABLE `semaforo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sensor`
--

DROP TABLE IF EXISTS `sensor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sensor` (
  `sensorID` int NOT NULL,
  `tipo` varchar(45) NOT NULL,
  `nombre` varchar(45) NOT NULL,
  `idDispositivo` int DEFAULT NULL COMMENT 'Hace referencia al sensor concreto pero no a sus valores',
  PRIMARY KEY (`sensorID`),
  KEY `sensor_device_idx` (`idDispositivo`),
  CONSTRAINT `sensor_device` FOREIGN KEY (`idDispositivo`) REFERENCES `semaforo` (`idSemaforo`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sensor`
--

LOCK TABLES `sensor` WRITE;
/*!40000 ALTER TABLE `sensor` DISABLE KEYS */;
INSERT INTO `sensor` VALUES (1,'Temp','termometro',1),(2,'Hum','Humedad',1),(3,'Temp','Termometro',2),(4,'Hum','Humedad',2);
/*!40000 ALTER TABLE `sensor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sensor-temperatura-humedad`
--

DROP TABLE IF EXISTS `sensor-temperatura-humedad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sensor-temperatura-humedad` (
  `sensor-temperaturaID` int NOT NULL,
  `accuracyTemp` float DEFAULT NULL,
  `valorTemp` float NOT NULL,
  `valorHumedad` float NOT NULL,
  `TimeStamp` bigint DEFAULT NULL,
  `sensorID` int DEFAULT NULL,
  `accuracyHum` float DEFAULT NULL,
  PRIMARY KEY (`sensor-temperaturaID`),
  KEY `sensorID_idx` (`sensorID`),
  CONSTRAINT `sensorTemHum_sensor` FOREIGN KEY (`sensorID`) REFERENCES `sensor` (`sensorID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sensor-temperatura-humedad`
--

LOCK TABLES `sensor-temperatura-humedad` WRITE;
/*!40000 ALTER TABLE `sensor-temperatura-humedad` DISABLE KEYS */;
/*!40000 ALTER TABLE `sensor-temperatura-humedad` ENABLE KEYS */;
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
  `Fnacimiento` bigint DEFAULT NULL,
  PRIMARY KEY (`idUsuario`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'Luismi','Soria','2452451T',12334242),(2,'Rosa','Rodriguez','2342514T',23524542);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `valores-sensor-contaminacion`
--

DROP TABLE IF EXISTS `valores-sensor-contaminacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `valores-sensor-contaminacion` (
  `idValor-sensor` int NOT NULL AUTO_INCREMENT,
  `sensorID` int NOT NULL,
  `valor` float NOT NULL,
  `accuracy` float NOT NULL COMMENT 'Mide la precision del sensor',
  `timeStamp` bigint DEFAULT NULL,
  PRIMARY KEY (`idValor-sensor`),
  KEY `valores_sensor_sensor_idx` (`sensorID`),
  CONSTRAINT `valores_sensor_sensor` FOREIGN KEY (`sensorID`) REFERENCES `sensor` (`sensorID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `valores-sensor-contaminacion`
--

LOCK TABLES `valores-sensor-contaminacion` WRITE;
/*!40000 ALTER TABLE `valores-sensor-contaminacion` DISABLE KEYS */;
INSERT INTO `valores-sensor-contaminacion` VALUES (1,1,37,1,45231234),(2,2,65,5,3424623345),(3,3,36,2,3462345323),(4,4,70,7,564562435);
/*!40000 ALTER TABLE `valores-sensor-contaminacion` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-03-26 13:54:36
