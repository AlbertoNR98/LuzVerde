#include <Arduino.h>
#include "ArduinoJson.h"
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <SoftwareSerial.h>
#include <stdlib.h>

int idCruce = 1;

char responseBuffer[300];
WiFiClient client;

String SSID = ""; //Poner el SSID
String PASS = ""; //Poner la contraseña

String SERVER_IP = "";  //Poner la IP del servidor
int SERVER_PORT = 8082; //Puerto API REST

void putValorSensorCont();
void putValorSensorTempHum();
void putLuz(const char*);

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
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/valores_sensor_contaminacion", true);
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
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/valores_sensor_temp_hum", true);
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
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/luces", true);
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
