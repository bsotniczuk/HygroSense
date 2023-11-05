package pl.bsotniczuk.hygrosense.controller;

import android.util.Log;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;
import pl.bsotniczuk.hygrosense.ConnectionType;
import pl.bsotniczuk.hygrosense.HygroEventListener;
import pl.bsotniczuk.hygrosense.MainActivity;
import pl.bsotniczuk.hygrosense.R;
import pl.bsotniczuk.hygrosense.aws.AwsIotCoreIntegration;
import pl.bsotniczuk.hygrosense.data.AppDatabase;
import pl.bsotniczuk.hygrosense.data.DbConstants;
import pl.bsotniczuk.hygrosense.data.model.SettingsItem;
import pl.bsotniczuk.hygrosense.data.viewmodel.SettingsViewModel;

public class DatabaseController {

    MainActivity mainActivity;
    SettingsViewModel settingsViewModel;
    AppDatabase appDatabase;
    public static SettingsItem settingsItem;

    public DatabaseController(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void initDbAndSetupAwsIotCoreConnection(HygroEventListener hygroEventListener) {
        this.appDatabase = Room.databaseBuilder(this.mainActivity.getApplicationContext(),
                AppDatabase.class, DbConstants.DATABASE_NAME).build();

        settingsViewModel = new ViewModelProvider(this.mainActivity).get(SettingsViewModel.class);

        final Observer<SettingsItem> settingsObserver = settingsItem -> {
            if (settingsItem != null) {
                DatabaseController.settingsItem = settingsItem;

                TextView connectionTextView = mainActivity.findViewById(R.id.connectionTextView);
                if (settingsItem.getConnection_type() == ConnectionType.AWS_IOT_CORE) {
                    mainActivity.runOnUiThread(() -> connectionTextView.setText(R.string.connecting_aws));
                    setupIotCoreIntegration(hygroEventListener, settingsItem);
                }
                else {
                    mainActivity.runOnUiThread(() -> connectionTextView.setText(R.string.connecting));
                }
                Log.i("HygroSense Db", "settings: wifi_ssid: " + settingsItem.getWifi_ssid() + " | esp32ipAddress:" + settingsItem.getEsp32_ip_address_access_point() + " | toString: " + settingsItem.toString());
            }
            else {
                Log.i("HygroSense Db", "Database is non existant");
                this.appDatabase = Room.databaseBuilder(this.mainActivity.getApplicationContext(),
                        AppDatabase.class, DbConstants.DATABASE_NAME).build();
                SettingsItem settingsItemInit = new SettingsItem(0, "http://192.168.4.1/", "", "", "anywz7kwswoml-ats.iot.eu-north-1.amazonaws.com","ESP32-Access-Point", "", ConnectionType.AWS_IOT_CORE, "");
                settingsViewModel.createSettingsItem(settingsItemInit);
            }
        };
        settingsViewModel.getSettingsItem().observe(this.mainActivity, settingsObserver);
    }

    private void setupIotCoreIntegration(HygroEventListener hygroEventListener, SettingsItem settingsItem) {
        AwsIotCoreIntegration awsIotCoreIntegration = new AwsIotCoreIntegration();
        awsIotCoreIntegration.setupAwsIotCoreConnection(
                mainActivity,
                settingsItem.getAws_iot_core_endpoint(),
                hygroEventListener
        );
    }

    //TODO: to delete
    public void readSettingsItem() {
        final Observer<SettingsItem> settingsObserver2 = settingsItem -> {
            if (settingsItem != null) {
                Log.i("HygroSense Db", "settings: wifi_ssid: " + settingsItem.getWifi_ssid() + " | esp32ipAddress:" + settingsItem.getEsp32_ip_address_access_point() + " | toString: " + settingsItem.toString());
            }
            else {
                Log.i("HygroSense Db", "I think that database is non existant");
                this.appDatabase = Room.databaseBuilder(this.mainActivity.getApplicationContext(),
                        AppDatabase.class, DbConstants.DATABASE_NAME).build();
                SettingsItem settingsItemInit = new SettingsItem(0, "http://192.168.4.1/", "", "", "anywz7kwswoml-ats.iot.eu-north-1.amazonaws.com", "ESP32-Access-Point", "", ConnectionType.AWS_IOT_CORE, "");
                settingsViewModel.createSettingsItem(settingsItemInit);
            }
        };
        settingsViewModel.getSettingsItem().observe(this.mainActivity, settingsObserver2);
    }
}
