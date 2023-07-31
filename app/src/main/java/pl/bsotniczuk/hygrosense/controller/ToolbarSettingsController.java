package pl.bsotniczuk.hygrosense.controller;

import android.app.ActivityOptions;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import pl.bsotniczuk.hygrosense.R;
import pl.bsotniczuk.hygrosense.SettingsActivity;

public class ToolbarSettingsController {

    public ToolbarSettingsController(SettingsActivity settingsActivity, Toolbar toolbar) {
//        settingsActivity.setSupportActionBar(toolbar);
//        settingsActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        settingsActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
//        toolbar.setDispl
        toolbar.inflateMenu(R.menu.menu_settings);
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_info) {
                Toast.makeText(
                                settingsActivity.getApplicationContext(),
                                "Info item clicked",
                                Toast.LENGTH_SHORT)
                        .show();
            } else if (id == android.R.id.home) {
                Toast.makeText(
                                settingsActivity.getApplicationContext(),
                                "Back button clicked",
                                Toast.LENGTH_SHORT)
                        .show();
            } else if (id == R.id.action_bluetooth) {
                settingsActivity.startBtAndConnectToEsp32();
            }
            return false;
        });
    }
}
