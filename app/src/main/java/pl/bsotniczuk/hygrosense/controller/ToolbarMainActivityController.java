package pl.bsotniczuk.hygrosense.controller;

import android.content.Intent;

import androidx.appcompat.widget.Toolbar;
import pl.bsotniczuk.hygrosense.MainActivity;
import pl.bsotniczuk.hygrosense.R;
import pl.bsotniczuk.hygrosense.SettingsActivity;
import pl.bsotniczuk.hygrosense.StatisticsActivity;
import pl.bsotniczuk.hygrosense.viewcontroller.MainActivityViewController;

public class ToolbarMainActivityController {

    public ToolbarMainActivityController(MainActivity mainActivity, Toolbar toolbar, MainActivityViewController mainActivityViewController) {
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_settings) {
                Intent intent = new Intent(mainActivity.getApplicationContext(), SettingsActivity.class);
                mainActivity.startActivity(intent);
                mainActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if (id == R.id.action_statistics) {
                Intent intent = new Intent(mainActivity.getApplicationContext(), StatisticsActivity.class);
                mainActivity.startActivity(intent);
                mainActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if (id == R.id.action_calibrate) {
                mainActivityViewController.openCameraTextRecognitionMenu();
            }
            return false;
        });
    }
}
