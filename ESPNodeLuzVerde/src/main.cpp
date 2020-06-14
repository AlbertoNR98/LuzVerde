#include <Arduino.h>
#include "ArduinoJson.h"
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <SoftwareSerial.h>
#include <stdlib.h>
#include <PubSubClient.h>

int idCruce = 1; //Probar ESP.getChipID o numero aleatorio, como en el ID de cliente MQTT (String clientId = String(random(0xffff), HEX);)

char responseBuffer[300];
WiFiClient espclient;
PubSubClient client(espclient);

String SSID = "ONO4AE9"; //Poner el SSID
String PASS = "xREAtH9aZ9ba"; //Poner la contraseña

String SERVER_IP = "192.168.1.252";  //Poner la IP del servidor API REST
int SERVER_PORT = 8082; //Puerto API REST

const char* mqtt_server= "192.168.1.252"; //Dirección del broker mqtt
const int mqtt_port = 1885;
const char* mqtt_user = "luzverde";
const char* mqtt_password = "ZeUS";
const char* mqtt_topic ="luces";

unsigned long lastMsg=0;
#define MSG_BUFFER_SIZE (50)
char msg[MSG_BUFFER_SIZE];
int value =0;

void putValorSensorCont();
void putValorSensorTempHum();
void putLuz(const char*);

//Supuestamente debe de leer lo que se envia a MQTT
void callback(char* topic , byte* payload, int lenght){
    Serial.print("Mensaje Recibido [");
    Serial.print(topic);
    Serial.print(" ]");

    for(int i = 0; i < lenght; i++){
      Serial.print((char)payload[i]);
    }
}

void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
    Serial.print("Iniciando conexión MQTT...");
    // ID cliente
    String clientId = String(ESP.getChipId());
    // Attempt to connect
    if (client.connect(clientId.c_str(), mqtt_user, mqtt_password)) {
      Serial.println("Conectado a MQTT");
    } else {
      Serial.print("Error, rc=");
      Serial.print(client.state());
      Serial.println(" Intentando conexión en 5 segundos...");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

void setup() {
  Serial.begin(9600);
  WiFi.begin(SSID, PASS);
  Serial.print("Conectando a WiFi...");
  while (WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }
  Serial.print("Conectado a WiFi. Dirección IP: ");
  Serial.print(WiFi.localIP());

  client.setServer(mqtt_server,mqtt_port);
  client.setCallback(callback);
}

void loop() {
  /*
  //Código de la API REST
  putValorSensorCont();
  delay(3000);
  putValorSensorTempHum();
  delay(3000);
  putLuz("Verde");
  delay(3000);
  putLuz("Rojo");
  delay(3000);
  */
  if(!client.connected()){
    reconnect();
  }
  client.loop();

  String payload = "{";

  payload += "\"idLuz_Semaforo\": ";
  payload += 1;
  payload += ", \"color\": ";
  payload += "\"Rojo""";
  payload += ", \"timestamp\": ";
  payload += 12131415;
  payload += ", \"idSemaforo\": ";
  payload += 1;

  payload += "}";

  Serial.println(payload);
  if (client.publish(mqtt_topic, (char*) payload.c_str())) {
    Serial.println("Publicación correcta");
  } else {
    Serial.println("Error en la publicación");
  }

  delay(5000);
}

//Funciones de la API REST utilizadas en la placa
void putValorSensorCont(){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(espclient, SERVER_IP, SERVER_PORT, "/api/valores_sensor_contaminacion", true);
    http.addHeader("Content-Type", "application/json");
    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(3) + 60;
    DynamicJsonDocument doc(capacity);
    doc["value"] = 2.4;
    doc["accuracy"] = 1.0;
    doc["idSensor"] = 3;

    String output;
    serializeJson(doc, output);

    int httpCode = http.PUT(output);

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    Serial.println("Resultado: " + payload);
  }
}

void putValorSensorTempHum(){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(espclient, SERVER_IP, SERVER_PORT, "/api/valores_sensor_temp_hum", true);
    http.addHeader("Content-Type", "application/json");

    const size_t capacity = JSON_OBJECT_SIZE(5) + JSON_ARRAY_SIZE(4) + 60;
    DynamicJsonDocument doc(capacity);
    doc["valueTemp"] = 41.3;
    doc["accuracyTemp"] = 1.0;
    doc["valueTemp"] = 80.2;
    doc["accuracyTemp"] = 1.0;
    doc["idSensor"] = 10;

    String output;
    serializeJson(doc, output);

    int httpCode = http.PUT(output);

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    Serial.println("Resultado: " + payload);
  }
}

void putLuz(const char* color){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(espclient, SERVER_IP, SERVER_PORT, "/api/luces", true);
    http.addHeader("Content-Type", "application/json");
    const size_t capacity = JSON_OBJECT_SIZE(2) + JSON_ARRAY_SIZE(2) + 60;
    DynamicJsonDocument doc(capacity);
    doc["color"] = color;
    doc["idSemaforo"] = 3;
    String output;
    serializeJson(doc, output);
    int httpCode = http.PUT(output);
    Serial.println("Response code: " + httpCode);
    String payload = http.getString();
    Serial.println("Resultado: " + payload);
  }
}
