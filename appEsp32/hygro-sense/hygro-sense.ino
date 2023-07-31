#include "WiFi.h"
#include "ESPAsyncWebServer.h"
#include <Adafruit_Sensor.h>
#include <DHT.h>

//wifi access point
//#include <WebServer.h>
#include <EEPROM.h>

const char* ssid = "";
const char* password = "";

#define DHTPIN 33
#define DHTTYPE DHT22

DHT dht(DHTPIN, DHTTYPE);

AsyncWebServer server(80);

String readDHTTemperature() {
  float t = dht.readTemperature();
  if (isnan(t)) {
    Serial.println("Failed to read from DHT sensor!");
    return "-";
  }
  else {
    Serial.println(t);
    return String(t);
  }
}

String readDHTHumidity() {
  float h = dht.readHumidity();
  if (isnan(h)) {
    Serial.println("Failed to read from DHT sensor!");
    return "-";
  }
  else {
    Serial.println(h);
    return String(h);
  }
}

void setup() {
  Serial.begin(115200);

  dht.begin();

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }
  Serial.println(WiFi.localIP());

  //TODO: to delete
  //server.on("/temperature", HTTP_GET, [](AsyncWebServerRequest *request){
  //  request->send_P(200, "text/plain", readDHTTemperature().c_str());
  //});
  //TODO: to delete
  //server.on("/humidity", HTTP_GET, [](AsyncWebServerRequest *request){
  //  request->send_P(200, "text/plain", readDHTHumidity().c_str());
  //});
  server.on("/data", HTTP_GET, [](AsyncWebServerRequest *request){
    String json = "{";
    json += "\"temperature\":"+String(readDHTTemperature().c_str());
    json += ",\"humidity\":"+String(readDHTHumidity().c_str());
    json += ",\"deviceName\":\""+String("ESP32")+"\"";
    json += "}";
    request->send(200, "application/json", json);
    json = String();
  });
  server.begin();
}

void loop() {}