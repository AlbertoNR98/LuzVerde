#include <Arduino.h>
#include "ArduinoJson.h"
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <SoftwareSerial.h>
#include <stdlib.h>
#include <PubSubClient.h>

int idCruce = 1;

char responseBuffer[300];
WiFiClient espclient;
PubSubClient client(espclient);

String SSID = ""; //Poner el SSID
String PASS = ""; //Poner la contraseña

const char* mqtt_server= ""; //Dirección del broker mqtt (noze-cual-es)

String SERVER_IP = "";  //Poner la IP del servidor
int SERVER_PORT = 8082; //Puerto API REST

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
    Serial.print("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = "ESP8266Client-";
    clientId += String(random(0xffff), HEX);
    // Attempt to connect
    if (client.connect(clientId.c_str())) {
      Serial.println("connected");
      // Once connected, publish an announcement...
      client.publish("outTopic", "hello world");
      // ... and resubscribe
      client.subscribe("inTopic");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

void setup() {
  Serial.begin(9600);
  WiFi.begin(SSID, PASS);
  Serial.print("Connecting...");
  while (WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }
  Serial.print("Connected, IP address: ");
  Serial.print(WiFi.localIP());

  client.setServer(mqtt_server,1885);
  client.setCallback(callback);
}

void loop() {
  putValorSensorCont();
  delay(3000);
  putValorSensorTempHum();
  delay(3000);
  putLuz("Verde");
  delay(3000);
  putLuz("Rojo");
  delay(3000);
}

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
