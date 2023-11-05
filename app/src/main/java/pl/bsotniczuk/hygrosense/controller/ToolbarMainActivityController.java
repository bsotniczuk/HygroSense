package pl.bsotniczuk.hygrosense.controller;

import android.content.Intent;

import androidx.appcompat.widget.Toolbar;
import pl.bsotniczuk.hygrosense.MainActivity;
import pl.bsotniczuk.hygrosense.R;
import pl.bsotniczuk.hygrosense.SettingsActivity;
import pl.bsotniczuk.hygrosense.StatisticsActivity;

public class ToolbarMainActivityController {

    public ToolbarMainActivityController(MainActivity mainActivity, Toolbar toolbar) {
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_settings) {

                Intent intent = new Intent(mainActivity.getApplicationContext(), SettingsActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainActivity.startActivity(intent);
                mainActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            else if (id == R.id.action_statistics) {

                Intent intent = new Intent(mainActivity.getApplicationContext(), StatisticsActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainActivity.startActivity(intent);
                mainActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            return false;
        });
    }
}
