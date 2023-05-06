package pl.bsotniczuk.hygrosense.controller;

import android.util.Log;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;
import pl.bsotniczuk.hygrosense.MainActivity;
import pl.bsotniczuk.hygrosense.data.AppDatabase;
import pl.bsotniczuk.hygrosense.data.DbConstants;
import pl.bsotniczuk.hygrosense.data.model.SettingsItem;
import pl.bsotniczuk.hygrosense.data.viewmodel.SettingsViewModel;

public class DatabaseController {

    MainActivity mainActivity;
    SettingsViewModel settingsViewModel;

    public DatabaseController(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        AppDatabase appDatabase = Room.databaseBuilder(this.mainActivity.getApplicationContext(),
                AppDatabase.class, DbConstants.DATABASE_NAME).build();
        settingsViewModel = new ViewModelProvider(this.mainActivity).get(SettingsViewModel.class);
//        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        readSettingsItem();
    }

    public void readSettingsItem() {
//        final Observer<SettingsItem> settingsObserver1 = settingsItem -> {
//            Log.i("HygroSense Db", "settings: wifi_ssid: " + settingsItem.getWifi_ssid() + " | esp32ipAddress:" + settingsItem.getEsp32_ip_address() + " | toString: " + settingsItem.toString());
//        };
//        settingsViewModel.getSettingsItem().observe(this.mainActivity, settingsObserver1);
//
//        LiveData<List<SettingsItem>> abcd = settingsViewModel.getAllSettingsItems();
//        Log.i("HygroSense Db", "db size: " + abcd.getValue().size());
    }
}
