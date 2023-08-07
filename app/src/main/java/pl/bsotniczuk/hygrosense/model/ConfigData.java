package pl.bsotniczuk.hygrosense.model;

public class ConfigData {

    private String wifi_password;
    private String wifi_ssid;

    public ConfigData() {
    }

    public ConfigData(String wifi_password, String wifi_ssid) {
        this.wifi_password = wifi_password;
        this.wifi_ssid = wifi_ssid;
    }

    public String getWifi_password() {
        return wifi_password;
    }

    public void setWifi_password(String wifi_password) {
        this.wifi_password = wifi_password;
    }

    public String getWifi_ssid() {
        return wifi_ssid;
    }

    public void setWifi_ssid(String wifi_ssid) {
        this.wifi_ssid = wifi_ssid;
    }
}
