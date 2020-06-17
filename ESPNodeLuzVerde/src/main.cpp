#include <Arduino.h>
#include "ArduinoJson.h"
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <SoftwareSerial.h>
#include <stdlib.h>
#include "DHT.h"

#define DHTTYPE DHT11

//Pines del MUX
const int muxSIG = A0;
const int muxS0 = 5;
const int muxS1 = 4;
const int muxS2 = 14;
//Variables para el semáforo
const int pinLuz1 = 12;
const int pinLuz2 = 13;
unsigned long delSem = 6000; //Delay máximo
unsigned long penDelSem = 0; //Penalización al delay en función de la contaminación
unsigned long previousDelSem;
bool ciclo = false; //Para controlar el ciclo del cruce
//Variables para la lectura del sensor de contaminación MQ135
unsigned long delMQ = 1000;
unsigned long previousDelMQ;
//Variables para el sensor de temperatura DHT11
const int DHTPin = 0;
unsigned long delDHT = 10000;
unsigned long previousDelDHT;
DHT dht(DHTPin, DHTTYPE);
//Variables para almacenar la información del cruce
int idCruce = (int)ESP.getChipId();
String nombreCruce = "";
String ipCruce = "";
long initialTimestamp = -1;
int idUsuario = -1;
bool registrado = false;  //La placa solo funciona si el proceso de registro ha sido correcto
int idSemaforos[4]; //Guarda los ID de los semáforos asociados a la placa
int idSensoresCont[4]; //Guarda los ID de los sensores de contaminación asociados a la placa
int idSensoresTempHum[4]; //Guarda los ID de los sensores de temperatura y humedad asociados a la placa

//Para API REST
char responseBuffer[300];
WiFiClient espclient;

String SSID = ""; //Poner el SSID
String PASS = ""; //Poner la contraseña de la red WiFi

String SERVER_IP = "";  //Poner la IP del servidor API REST
int REST_PORT = 8082; //Puerto API REST

void conectaWiFi();

bool initCruce();
bool compruebaRegistro();
void actualizaIPCruce();

void getSemaforos();
int getSensorContaminacion(int);
int getSensorTempHum(int);

void controlSemaforo();
void SetMuxChannel();
void leeMux();
void leeDHT();

void putValorSensorCont(float, int);
void putValorSensorTempHum(float, float, int);
void putLuz(const char*, int);

void setup() {
  Serial.begin(9600);
  dht.begin();
  conectaWiFi();
  //Registra la IP asignada y comprueba qué semáforos y sensores están asociados al cruce
  registrado = initCruce();
  pinMode(DHTPin, INPUT);
  pinMode(muxSIG, INPUT);
  pinMode(muxS0, OUTPUT);
  pinMode(muxS1, OUTPUT);
  pinMode(muxS2, OUTPUT);
  pinMode(pinLuz1, OUTPUT);
  pinMode(pinLuz2, OUTPUT);
  previousDelSem = millis();
  previousDelMQ = millis();
  previousDelDHT = millis();
}

void loop() {
  if(registrado){
    unsigned long currentMillis = millis();
    //Tarea 1: Controla las luces del semáforo
    if((unsigned long)(currentMillis - previousDelSem) >= (delSem - penDelSem)){
      controlSemaforo();
      previousDelSem = millis();
    }
    //Tarea 2: Lee los sensores de CO2
    if((unsigned long)(currentMillis - previousDelMQ) >= delMQ){
      leeMux();
      previousDelMQ = millis();
    }
    //Tarea 3: Lee el sensor de temperatura y humedad
    if((unsigned long)(currentMillis - previousDelDHT) >= delDHT){
      leeDHT();
      previousDelDHT = millis();
    }
  }else{
    Serial.println("Cruce no registrado. Regístrelo en Telegram y reinicie el sistema.");
    delay(5000);
  }
}

//Control del hardware del semáforo
void controlSemaforo(){
  ciclo = !ciclo;
  if(ciclo){
    digitalWrite(pinLuz1, HIGH);
    digitalWrite(pinLuz2, LOW);
    putLuz("Verde", idSemaforos[0]);
    putLuz("Verde", idSemaforos[1]);
    putLuz("Rojo", idSemaforos[2]);
    putLuz("Rojo", idSemaforos[3]);
  }else{
    digitalWrite(pinLuz1, LOW);
    digitalWrite(pinLuz2, HIGH);
    putLuz("Rojo", idSemaforos[0]);
    putLuz("Rojo", idSemaforos[1]);
    putLuz("Verde", idSemaforos[2]);
    putLuz("Verde", idSemaforos[3]);
  }
}

void SetMuxChannel(byte channel)
{
   digitalWrite(muxS0, bitRead(channel, 0));
   digitalWrite(muxS1, bitRead(channel, 1));
   digitalWrite(muxS2, bitRead(channel, 2));
}

void leeMux(){
  int totalRead = 0;
  int j = 0;
  for (byte i = 0; i < 7; i=i+2) //Entre pines, GND para evitar ruido
   {
      SetMuxChannel(i);
      int muxValue = analogRead(muxSIG);
      totalRead = totalRead + muxValue;
      delay(200);
      putValorSensorCont((float)muxValue, (int)idSensoresCont[j]);
      j++;
   }
   Serial.println();

   //Niveles de contaminación
   if(totalRead > 1700){
     penDelSem = (int)(0.5*delSem); //Como máximo, el cruce cambia el ciclo el doble de rápido
   }else if(totalRead > 1500 && totalRead <=1700){
     penDelSem = (int)(0.35*delSem);
   }else if(totalRead > 750 && totalRead <= 1500){
     penDelSem = (int)(0.15*delSem);
   }else{
      penDelSem = 0;  //No hay nada de contaminación -> El cruce sigue el ciclo establecido
   }

   Serial.println("\nValor total alcanzado: "+String(totalRead));
   Serial.println("Delay del semáforo establecido:" +String(delSem)+ "ms");
   Serial.println("Penalización por contaminación:" +String(penDelSem)+ "ms");
   Serial.println("Tiempo entre ciclos semafóricos: "+String(delSem-penDelSem)+ "ms\n");

}

void leeDHT(){
  float h = dht.readHumidity();
  float t = dht.readTemperature();

   if (isnan(h) || isnan(t)) {
      Serial.println("Error al leer del sensor de temperatura");
      return;
   }

   //Inserta en la API REST
   for(byte i = 0; i<4; i++){
     putValorSensorTempHum(t, h, (int)idSensoresTempHum[i]); //Solo lee un sensor y pone el valor en los cuatro semáforos, ya que la temperatura varía menos (ahorro de pines)
   }                                                         //Si hubiese cuatro sensores, se llamaría a leeDHT cuatro veces y el bucle for desaparecería (similar a la lectura de los cuatro sensores MQ135)
}

//Conecta la placa al WiFi
void conectaWiFi(){
  WiFi.begin(SSID, PASS);
  Serial.print("ID de placa: ");
  Serial.println(ESP.getChipId());
  Serial.print("Conectando a WiFi...");
  while (WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }
  Serial.print("Conectado a WiFi. Dirección IP: ");
  Serial.println(WiFi.localIP());
}

bool initCruce(){
  bool res = true;
  bool registroOK = compruebaRegistro();
  if(registroOK){
    //Asigna la IP actual
    actualizaIPCruce();
    //Busca los semáforos asociados al cruce
    getSemaforos();
    //Busca los ID de los semáforos de contaminación y de temperatura
    for(byte i = 0; i<4; i++){
      idSensoresCont[i] = getSensorContaminacion(idSemaforos[i]);
      idSensoresTempHum[i] = getSensorTempHum(idSemaforos[i]);
    }
  }else{
    res = false;
  }
  return res;
}

bool compruebaRegistro(){
  bool res = false;
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    int idC;
    char enl[30];
    sprintf(enl, "/api/cruce/%d", idCruce);
    http.begin(espclient, SERVER_IP, REST_PORT, enl, true);
    int httpCode = http.GET();

    Serial.println("Código de respuesta: " + String(httpCode));

    String payload = http.getString();

    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(12) + 200;
    DynamicJsonDocument doc(capacity);

    DeserializationError error = deserializeJson(doc, payload);
    if (error){
      Serial.print("Error al deserializar: ");
      Serial.println(error.c_str());
      return false;
    }

    idC = doc[0]["idCruce"].as<int>();
    ipCruce = doc[0]["ipCruce"].as<char*>();
    nombreCruce = doc[0]["nombreCruce"].as<char*>();
    initialTimestamp = doc[0]["initialTimestamp"].as<long>();
    idUsuario = doc[0]["idUsuario"].as<int>();

    if(idC == idCruce){
      Serial.println("\n-------Cruce con ID " + String(idC)+" encontrado-------");
      Serial.println("\tIP asignada: "+ipCruce);
      Serial.println("\tNombre del cruce: "+nombreCruce);
      Serial.println("\tTimestamp inicial: "+String(initialTimestamp));
      Serial.println("\tUsuario que lo ha introducido: "+String(idUsuario));
      res = true;
    }else{
      Serial.println("Cruce no registrado. Regístrelo en Telegram y reinicie el sistema.");
    }
  }
    return res;
}

void actualizaIPCruce(){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    char enl[40];
    ipCruce = WiFi.localIP().toString();
    sprintf(enl, "/api/cruces/%d", idCruce);
    http.begin(espclient, SERVER_IP, REST_PORT, enl, true);
    http.addHeader("Content-Type", "application/json");
    const size_t capacity = JSON_OBJECT_SIZE(4) + JSON_ARRAY_SIZE(5) + 70;
    DynamicJsonDocument doc(capacity);
    doc["ipCruce"] = ipCruce;
    doc["nombreCruce"] = nombreCruce;
    doc["initialTimestamp"] = initialTimestamp;
    doc["idUsuario"] = idUsuario;

    String output;
    serializeJson(doc, output);

    int httpCode = http.PUT(output);
    Serial.println("Código de respuesta: " + httpCode);

    String payload = http.getString();
    //Serial.println("Resultado: " + payload);
    Serial.println("IP actualizada con éxito");
  }
}
//Obtiene los sensores y semáforos asociados
void getSemaforos(){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    String nombreSemaforo[4];
    char enl[30];
    sprintf(enl, "/api/semaforos/%d", idCruce);
    http.begin(espclient, SERVER_IP, REST_PORT, enl, true);
    int httpCode = http.GET();

    Serial.println("\nObteniendo los semáforos asociados...");
    Serial.println("Código de respuesta: " + String(httpCode));

    String payload = http.getString();

    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(12) + 200;
    DynamicJsonDocument doc(capacity);

    DeserializationError error = deserializeJson(doc, payload);
    if (error){
      Serial.print("Error al deserializar: ");
      Serial.println(error.c_str());
      return;
    }

    for(byte i = 0; i<4;i++){ //4 semáforos siempre (obligado por el bot)
      Serial.println("-------Semáforo " + String(i)+"-------");
      idSemaforos[i] = doc[i]["idSemaforo"].as<int>();
      nombreSemaforo[i] = doc[i]["nombreSemaforo"].as<char*>();

      Serial.println("\tID Semáforo: "+String(idSemaforos[i]));
      Serial.println("\tID Cruce: "+String(idCruce));
      Serial.println("\tNombre: "+nombreSemaforo[i]);
    }
  }
}

int getSensorContaminacion(int idSemaforo){
  int idSensor = -1;
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    String nombreSensor;
    char enl[30];
    sprintf(enl, "/api/sensores/%d/%s", idSemaforo, "CO2");
    http.begin(espclient, SERVER_IP, REST_PORT, enl, true);
    int httpCode = http.GET();

    Serial.println("\nObteniendo los sensores de contaminación del semáforo "+String(idSemaforo)+"...");
    Serial.println("Código de respuesta: " + String(httpCode));

    String payload = http.getString();

    const size_t capacity = JSON_OBJECT_SIZE(4) + JSON_ARRAY_SIZE(4) + 60;
    DynamicJsonDocument doc(capacity);

    DeserializationError error = deserializeJson(doc, payload);
    if (error){
      Serial.print("Error al deserializar: ");
      Serial.println(error.c_str());
      return -1;
    }

      Serial.println("-------Sensor de contaminación del semáforo " + String(idSemaforo)+"-------");
      idSensor = doc[0]["idSensor"].as<int>();
      nombreSensor = doc[0]["nombreSensor"].as<char*>();

      Serial.println("\tID Semáforo: "+String(idSemaforo));
      Serial.println("\tID Sensor: "+String(idSensor));
      Serial.println("\tNombre: "+nombreSensor);
    }
    return idSensor;
  }


int getSensorTempHum(int idSemaforo){
  int idSensor = -1;
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    String nombreSensor;
    char enl[30];
    sprintf(enl, "/api/sensores/%d/%s", idSemaforo, "TempHum");
    http.begin(espclient, SERVER_IP, REST_PORT, enl, true);
    int httpCode = http.GET();

    Serial.println("\nObteniendo los sensores de temperatura y humedad del semáforo "+String(idSemaforo)+"...");
    Serial.println("Código de respuesta: " + String(httpCode));

    String payload = http.getString();

    const size_t capacity = JSON_OBJECT_SIZE(4) + JSON_ARRAY_SIZE(4) + 60;
    DynamicJsonDocument doc(capacity);

    DeserializationError error = deserializeJson(doc, payload);
    if (error){
      Serial.print("Error al deserializar: ");
      Serial.println(error.c_str());
      return -1;
    }

      Serial.println("-------Sensor de temperatura y humedad del semáforo " + String(idSemaforo)+"-------");
      idSensor = doc[0]["idSensor"].as<int>();
      nombreSensor = doc[0]["nombreSensor"].as<char*>();

      Serial.println("\tID Semáforo: "+String(idSemaforo));
      Serial.println("\tID Sensor: "+String(idSensor));
      Serial.println("\tNombre: "+nombreSensor);
    }
    return idSensor;
}

//Funciones de la API REST utilizadas en la placa
void putValorSensorCont(float value, int idSensor){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(espclient, SERVER_IP, REST_PORT, "/api/valores_sensor_contaminacion", true);
    http.addHeader("Content-Type", "application/json");
    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(4) + 60;
    DynamicJsonDocument doc(capacity);
    doc["value"] = value;
    doc["accuracy"] = 1.0;
    doc["idSensor"] = idSensor;

    String output;
    serializeJson(doc, output);

    int httpCode = http.PUT(output);
    Serial.println("\nNuevo valor del sensor de contaminación "+String(idSensor)+"...");
    Serial.println("Código de respuesta: " + httpCode);

    String payload = http.getString();
    Serial.println("Resultado: " + payload);
  }
}

void putValorSensorTempHum(float valueTemp, float valueHum, int idSensor){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(espclient, SERVER_IP, REST_PORT, "/api/valores_sensor_temp_hum", true);
    http.addHeader("Content-Type", "application/json");

    const size_t capacity = JSON_OBJECT_SIZE(5) + JSON_ARRAY_SIZE(4) + 60;
    DynamicJsonDocument doc(capacity);
    doc["valueTemp"] = valueTemp;
    doc["accuracyTemp"] = 0.7;
    doc["valueHum"] = valueHum;
    doc["accuracyHum"] = 0.7;
    doc["idSensor"] = idSensor;

    String output;
    serializeJson(doc, output);

    int httpCode = http.PUT(output);
    Serial.println("\nNuevo valor del sensor de temperatura y humedad "+String(idSensor)+"...");
    Serial.println("Código de respuesta: " + httpCode);

    String payload = http.getString();
    Serial.println("Resultado: " + payload);
  }
}

void putLuz(const char* color, int idSemaforo){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(espclient, SERVER_IP, REST_PORT, "/api/luces", true);
    http.addHeader("Content-Type", "application/json");

    const size_t capacity = JSON_OBJECT_SIZE(2) + JSON_ARRAY_SIZE(4) + 60;
    DynamicJsonDocument doc(capacity);
    doc["color"] = color;
    doc["idSemaforo"] = idSemaforo;

    String output;
    serializeJson(doc, output);

    int httpCode = http.PUT(output);
    Serial.println("\nNuevo estado del semáforo "+String(idSemaforo)+"...");
    Serial.println("Código de respuesta: " + httpCode);

    String payload = http.getString();
    Serial.println("Resultado: " + payload);
  }
}
