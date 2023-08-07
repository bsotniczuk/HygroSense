package pl.bsotniczuk.hygrosense;

import androidx.appcompat.app.AppCompatActivity;
import pl.bsotniczuk.hygrosense.data.DbConstants;
import pl.bsotniczuk.hygrosense.viewcontroller.MainActivityViewController;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    //TODO: Test wifi connection based on settings
    //TODO: encrypt wifi password in database and hide when entering in settings
    //TODO: Bluetooth connection with esp32 to exchange data (send data, and receive if they were received correctly)
    //TODO: add disconnected animation when the phone cannot get data from esp32 for 30 seconds

    //TODO: save ESP32 ssid and password to EEPROM, restart, then load from EEPROM
    //TODO: do an AWS core endpoint to be able to access the ESP32 from anywhere in the world
    //TODO: access AWS endpoint from this app
    //TODO: add POST that will be able to send ssid and password to ESP32 using its access point capabilities
    //TODO: add accesspoint mode so it can also read data from 192.168.4.1
    //TODO: HygroEventListener set for AwsIotCoreIntegration
    //TODO: delete bluetooth related functionality

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        new MainActivityViewController(this);
    }

    public void deleteDb(View view) {
        this.deleteDatabase(DbConstants.DATABASE_NAME);
    }

    public void readSettingsItem(View view) {
        MainActivityViewController.databaseController.readSettingsItem();
    }

    public void launchSettings(View view) {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}
