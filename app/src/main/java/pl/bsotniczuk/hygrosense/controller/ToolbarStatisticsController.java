package pl.bsotniczuk.hygrosense.controller;

import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import pl.bsotniczuk.hygrosense.R;
import pl.bsotniczuk.hygrosense.StatisticsActivity;

public class ToolbarStatisticsController {

    private StatisticsDbController statisticsDbController;

    public ToolbarStatisticsController(StatisticsActivity statisticsActivity, Toolbar toolbar) {
        toolbar.inflateMenu(R.menu.menu_statistics);
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_delete) {
                statisticsDbController = new StatisticsDbController(statisticsActivity);
                statisticsDbController.deleteAllFromStatisticsTable();
                Toast.makeText(statisticsActivity.getApplicationContext(), R.string.deleted_all_statistics, Toast.LENGTH_SHORT).show();
            }
            return false;
        });
    }
}
