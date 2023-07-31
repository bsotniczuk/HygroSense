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

    private ApiFetcher apiFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.apiFetcher = new ApiFetcher();

        setContentView(R.layout.activity_main);

        MainActivityViewController mainActivityViewController =
                new MainActivityViewController(
                        this,
                        this.apiFetcher
                );
        this.apiFetcher.addListener(mainActivityViewController);
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
