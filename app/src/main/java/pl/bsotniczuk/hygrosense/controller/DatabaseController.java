package pl.bsotniczuk.hygrosense.controller;

import android.util.Log;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;
import pl.bsotniczuk.hygrosense.MainActivity;
import pl.bsotniczuk.hygrosense.data.AppDatabase;
import pl.bsotniczuk.hygrosense.data.DbConstants;
import pl.bsotniczuk.hygrosense.data.model.SettingsItem;
import pl.bsotniczuk.hygrosense.data.viewmodel.SettingsViewModel;
import pl.bsotniczuk.hygrosense.viewcontroller.MainActivityViewController;

public class DatabaseController {

    MainActivity mainActivity;
    SettingsViewModel settingsViewModel;
    AppDatabase appDatabase;
    static SettingsItem settingsItem;

    public DatabaseController(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        initDb();
    }

    private void initDb() {
        this.appDatabase = Room.databaseBuilder(this.mainActivity.getApplicationContext(),
                AppDatabase.class, DbConstants.DATABASE_NAME).build();

        settingsViewModel = new ViewModelProvider(this.mainActivity).get(SettingsViewModel.class);

        final Observer<SettingsItem> settingsObserver = settingsItem -> {
            if (settingsItem != null) {
                DatabaseController.settingsItem = settingsItem;
                MainActivityViewController.ipAddress = settingsItem.getEsp32_ip_address();
                Log.i("HygroSense Db", "settings: wifi_ssid: " + settingsItem.getWifi_ssid() + " | esp32ipAddress:" + settingsItem.getEsp32_ip_address() + " | toString: " + settingsItem.toString());
            }
            else {
                Log.i("HygroSense Db", "I think that database is non existant");
                this.appDatabase = Room.databaseBuilder(this.mainActivity.getApplicationContext(),
                        AppDatabase.class, DbConstants.DATABASE_NAME).build();
                SettingsItem settingsItemInit = new SettingsItem(0, "http://192.168.1.16/", "", "", "");
                MainActivityViewController.ipAddress = settingsItemInit.getEsp32_ip_address();
                settingsViewModel.createSettingsItem(settingsItemInit);
            }
        };
        settingsViewModel.getSettingsItem().observe(this.mainActivity, settingsObserver);
    }

    //TODO: to delete
    public void readSettingsItem() {
        final Observer<SettingsItem> settingsObserver2 = settingsItem -> {
            if (settingsItem != null) {
                Log.i("HygroSense Db", "settings: wifi_ssid: " + settingsItem.getWifi_ssid() + " | esp32ipAddress:" + settingsItem.getEsp32_ip_address() + " | toString: " + settingsItem.toString());
            }
            else {
                Log.i("HygroSense Db", "I think that database is non existant");
                this.appDatabase = Room.databaseBuilder(this.mainActivity.getApplicationContext(),
                        AppDatabase.class, DbConstants.DATABASE_NAME).build();
                SettingsItem settingsItemInit = new SettingsItem(0, "1xd http://192.168.1.16/", "", "", "");
                settingsViewModel.createSettingsItem(settingsItemInit);
            }
        };
        settingsViewModel.getSettingsItem().observe(this.mainActivity, settingsObserver2);
    }
}
