###### HygroSense - About the repository:
- This app is a working IoT measurement system based on ESP32 and an Android application
ESP32 can communicate with the internet via a WiFi module, it publishes to an AWS IoT Core endpoint that is accessible online
The Android application has an ability to set ESP32 network settings and is able to fetch data from an ESP32 by subscribing to an AWS IoT Core or accessing ESP32 in AccessPoint mode when physically nearby
Code for the ESP32 can be found in the appEsp32 folder (hygro-sense.ino)
- Upon first launch, user has to connect to the ESP32 in Access Point mode and pass WiFi SSID and WiFi password, 
since those will be empty on first launch of the ESP32