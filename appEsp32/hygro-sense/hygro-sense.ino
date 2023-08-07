#include "WiFi.h"
#include "ESPAsyncWebServer.h"
#include <Adafruit_Sensor.h>
#include <DHT.h>

//AWS
#include "secrets.h"
#include <WiFiClientSecure.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>

#include <ESP32Ping.h>

//EEPROM
#include <EEPROM.h>
#define eepromTextVariableSize 33 // the max size of the ssid, password etc. 32+null terminated

#define DHTPIN 33
#define DHTTYPE DHT22

#define BUTTON_PIN 0 //GPIO0, boot button

#define AWS_IOT_PUBLISH_TOPIC   "hygrosense/pub"
#define AWS_IOT_SUBSCRIBE_TOPIC "hygrosense/sub"

char _ssid[eepromTextVariableSize] = "";
char _password[eepromTextVariableSize] = "";

const char* ssidAccessPoint = "ESP32-Access-Point";
const char* passwordAccessPoint = "HygroSenseApp1234";

float Target;

DHT dht(DHTPIN, DHTTYPE);
AsyncWebServer server(80);

WiFiClientSecure net = WiFiClientSecure();
PubSubClient client(net);

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

void configPostEndpoint(AsyncWebServerRequest *request) {
  Serial.println("POST has been received!");
  int parameterCount = 0;

  int params = request->params();
  for (int i = 0; i < params; i++) {
    AsyncWebParameter* p = request->getParam(i);
    String parameter = p->name();
    String value = p->value();
    Serial.printf("POST[%s]: %s\n", p->name().c_str(), p->value().c_str());
    if (parameter.equals("wifi_ssid")) {
      value.toCharArray(_ssid, eepromTextVariableSize);
      parameterCount++;     

      size_t valueLength = strlen(value.c_str());
      if (valueLength > 31 || valueLength < 3) {
        parameterCount = 0; //invalidate storing in EEPROM memory
        sendJsonResponse(200, "Value length is too small or too big", request);
      }
    }
    if (parameter.equals("wifi_password")) {
      value.toCharArray(_password, eepromTextVariableSize);
      parameterCount++;     

      size_t valueLength = strlen(value.c_str());
      if (valueLength > 31 || valueLength < 3) {
        parameterCount = 0; //invalidate storing in EEPROM memory
        sendJsonResponse(200, "Value length is too small or too big", request);
      }
    }
  }
  if (parameterCount == 2) {
    Serial.println("\n_ssid: ");
    Serial.print(_ssid);
    Serial.println("\n_password: ");
    Serial.println(_password);
    sendJsonResponse(200, "ESP32 - ok!", request);
    saveSettingsToEEPPROM(_ssid, _password);

    ESP.restart(); //restart after getting new wifi settings
  }
  else {
    sendJsonResponse(200, "Request is wrong or contains too few parameters", request);
  }
}

void sendJsonResponse(int responseCode, String response, AsyncWebServerRequest *request) {
  String json = "{";
  json += "\"response\":\""+String(response)+"\"";
  json += "}";
  Serial.println(json);
  request->send(responseCode, "application/json", json);
  json = String();
}

void connectToWiFiAndRunServer(const char* ssid, const char* password) {
  WiFi.mode(WIFI_AP_STA);
  WiFi.begin(ssid, password);
  WiFi.softAP(ssidAccessPoint, passwordAccessPoint);
  Serial.print("Access Point gateway IP: ");
  Serial.println(WiFi.softAPIP());

  server.on("/config", HTTP_POST, configPostEndpoint);
  server.on("/data", HTTP_GET, [](AsyncWebServerRequest *request) {
    String json = "{";
    json += "\"temperature\":"+String(readDHTTemperature().c_str());
    json += ",\"humidity\":"+String(readDHTHumidity().c_str());
    json += ",\"deviceName\":\""+String("ESP32")+"\"";
    json += "}";
    request->send(200, "application/json", json);
    json = String();
  });
  server.begin();

  int iterator = 0;
  while (WiFi.status() != WL_CONNECTED) {
    delay(2000);
    Serial.println("Connecting to WiFi...");
    iterator++;
  }
  Serial.println(WiFi.localIP());

  //ping
  bool success = Ping.ping("www.google.com", 3);
  if(!success){
    Serial.println("Ping failed");
    return;
  }
  Serial.println("Ping successful.");

  connectToAwsIot();
}

void connectToAwsIot() {
  // Configure WiFiClientSecure to use the AWS IoT device credentials
  net.setCACert(AWS_CERT_CA);
  net.setCertificate(AWS_CERT_CRT);
  net.setPrivateKey(AWS_CERT_PRIVATE);
 
  // Connect to the MQTT broker on the AWS endpoint we defined earlier
  client.setServer(AWS_IOT_ENDPOINT, 8883);
 
  // Create a message handler
  client.setCallback(messageHandler);
 
  Serial.println("Connecting to AWS IOT");
 
  while (!client.connect(THINGNAME)) {
    Serial.print(".");
    delay(100);
  }
 
  if (!client.connected()) {
    Serial.println("AWS IoT Timeout!");
    return;
  }
 
  // Subscribe to a topic
  client.subscribe(AWS_IOT_SUBSCRIBE_TOPIC);
 
  Serial.println("AWS IoT Connected!");
}

void messageHandler(char* topic, byte* payload, unsigned int length) {
  Serial.print("incoming: ");
  Serial.println(topic);
 
  StaticJsonDocument<200> doc;
  deserializeJson(doc, payload);
  const char* message = doc["message"];
  Serial.println(message);
}

void publishMessageToAwsIot(String humidity, String temperature) {
  StaticJsonDocument<200> doc;
  doc["temperature"] = temperature;
  doc["humidity"] = humidity;
  char jsonBuffer[512];
  serializeJson(doc, jsonBuffer); // print to client
 
  client.publish(AWS_IOT_PUBLISH_TOPIC, jsonBuffer);
}

#define eepromBufferSize 200

void saveSettingsToEEPPROM(char* ssid_, char* pass_) {
  Serial.println("\n============ saveSettingsToEEPPROM");
  writeEEPROM(1 * eepromTextVariableSize, eepromTextVariableSize, ssid_);
  writeEEPROM(2 * eepromTextVariableSize, eepromTextVariableSize,  pass_);
}

void readSettingsFromEEPROM(char* ssid_, char* pass_) {
  readEEPROM( 1 * eepromTextVariableSize, eepromTextVariableSize, ssid_);
  readEEPROM( (2 * eepromTextVariableSize), eepromTextVariableSize, pass_);

  Serial.println("\n============ readSettingsFromEEPROM");
  Serial.println(ssid_);
  Serial.println(pass_);
}

void writeEEPROM(int startAdr, int length, char* writeString) {
  EEPROM.begin(eepromBufferSize);
  yield();
  for (int i = 0; i < length; i++) EEPROM.write(startAdr + i, writeString[i]);
  EEPROM.commit();
  EEPROM.end();
}

void readEEPROM(int startAdr, int maxLength, char* dest) {
  EEPROM.begin(eepromBufferSize);
  delay(10);
  for (int i = 0; i < maxLength; i++) dest[i] = char(EEPROM.read(startAdr + i));
  dest[maxLength - 1] = 0;
  EEPROM.end();
}

void setup() {
  Serial.begin(115200);
  while (!Serial) {}
  pinMode(BUTTON_PIN, INPUT);

  dht.begin();

  //in case EEPROM is empty on first launch
  EEPROM.get(0, Target);
  if (isnan(Target))
  {
    Serial.println(F("Target is 'nan' so run wifi with empty values."));
    connectToWiFiAndRunServer("", "");
  }
  readSettingsFromEEPROM(_ssid, _password);
  Serial.println(_ssid);
  Serial.println(_password); //to delete, exposes password

  connectToWiFiAndRunServer(_ssid, _password);
}

void loop() {
  publishMessageToAwsIot(readDHTHumidity(), readDHTTemperature());
  client.loop();
  delay(6000); //change to 2000 -> 2s
}