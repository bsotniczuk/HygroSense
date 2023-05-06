package pl.bsotniczuk.hygrosense;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;
import pl.bsotniczuk.hygrosense.data.AppDatabase;
import pl.bsotniczuk.hygrosense.data.DbConstants;
import pl.bsotniczuk.hygrosense.data.model.SettingsItem;
import pl.bsotniczuk.hygrosense.data.viewmodel.SettingsViewModel;
import pl.bsotniczuk.hygrosense.viewcontroller.MainActivityViewController;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    //TODO: settings menu
    //TODO: Test wifi connection based on settings
    //TODO: encrypt wifi password in database and hide when entering in settings
    //TODO: Bluetooth connection with esp32 to exchange data
    //TODO: move database controller and database related methods to DatabaseController and use them in MainActivityViewController

    private ApiFetcher apiFetcher;
    private SettingsViewModel settingsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.apiFetcher = new ApiFetcher();
        Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, DbConstants.DATABASE_NAME).build();
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        setContentView(R.layout.activity_main);

        MainActivityViewController mainActivityViewController =
                new MainActivityViewController(this,
                        this.apiFetcher,
                        findViewById(R.id.temperatureValueTextView),
                        findViewById(R.id.humidityValueTextView),
                        settingsViewModel
                );
        this.apiFetcher.addListener(mainActivityViewController);
    }

    private boolean checkIfDbExists() {

        return true;
    }

    public void addToSettingsDb(View view) {
        SettingsItem settingsItem = new SettingsItem(0, "http://192.168.1.16/", "Storczykowa 45_1", "", "");
        settingsViewModel.createSettingsItem(settingsItem);
    }

    public void deleteDb(View view) {
        this.deleteDatabase(DbConstants.DATABASE_NAME);
    }

    public void readSettingsItem(View view) {
//        settingsViewModel.getSettingsItem();
        this.readSettingsItem();
    }

    private void readSettingsItem() {
        final Observer<SettingsItem> settingsObserver = settingsItem -> {
            Log.i("HygroSense Db", "settings: wifi_ssid: " + settingsItem.getWifi_ssid() + " | esp32ipAddress:" + settingsItem.getEsp32_ip_address() + " | toString: " + settingsItem.toString());
        };
        settingsViewModel.getSettingsItem().observe(this, settingsObserver);
    }

}