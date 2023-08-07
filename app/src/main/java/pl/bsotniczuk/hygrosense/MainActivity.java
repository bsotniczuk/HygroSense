package pl.bsotniczuk.hygrosense;

import androidx.appcompat.app.AppCompatActivity;
import pl.bsotniczuk.hygrosense.data.DbConstants;
import pl.bsotniczuk.hygrosense.viewcontroller.MainActivityViewController;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

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
