package pl.bsotniczuk.hygrosense.controller;

import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import pl.bsotniczuk.hygrosense.R;
import pl.bsotniczuk.hygrosense.StatisticsActivity;

public class ToolbarStatisticsController {

    public ToolbarStatisticsController(StatisticsActivity statisticsActivity, Toolbar toolbar) {
        toolbar.inflateMenu(R.menu.menu_statistics);
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_delete) {
                Toast.makeText(
                                statisticsActivity.getApplicationContext(),
                                "Delete item clicked",
                                Toast.LENGTH_SHORT)
                        .show();
            }
            return false;
        });
    }
}
