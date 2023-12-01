package pl.bsotniczuk.hygrosense.controller;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;
import pl.bsotniczuk.hygrosense.HygroEventListener;
import pl.bsotniczuk.hygrosense.MainActivity;
import pl.bsotniczuk.hygrosense.StatisticsActivity;
import pl.bsotniczuk.hygrosense.StatisticsDbEventListener;
import pl.bsotniczuk.hygrosense.data.AppDatabase;
import pl.bsotniczuk.hygrosense.data.DbConstants;
import pl.bsotniczuk.hygrosense.data.model.StatisticsItem;
import pl.bsotniczuk.hygrosense.data.viewmodel.StatisticsViewModel;
import pl.bsotniczuk.hygrosense.model.SensorData;

import static pl.bsotniczuk.hygrosense.viewcontroller.MainActivityViewController.faultyValue;

public class StatisticsDbController {

    private MainActivity mainActivity;
    private AppDatabase appDatabase;
    private StatisticsViewModel statisticsViewModel;

    public StatisticsDbController(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.appDatabase = Room.databaseBuilder(this.mainActivity.getApplicationContext(),
                AppDatabase.class, DbConstants.DATABASE_NAME).build();
        statisticsViewModel = new ViewModelProvider(this.mainActivity).get(StatisticsViewModel.class);
    }

    public StatisticsDbController(StatisticsActivity statisticsActivity) {
        this.appDatabase = Room.databaseBuilder(statisticsActivity.getApplicationContext(),
                AppDatabase.class, DbConstants.DATABASE_NAME).build();
        statisticsViewModel = new ViewModelProvider(statisticsActivity).get(StatisticsViewModel.class);
        addListener(statisticsActivity);

        Log.i("HygroSense", "before read");
        readDataFromDatabaseNotArchivedSortByDate(statisticsActivity);
    }

    public void saveToStatisticsTable(SensorData[] sensorData) {
        Date date = new Date();

        boolean isAllDataCorrect =
                Arrays.stream(sensorData).allMatch(sensor ->
                        sensor.getTemperature() > faultyValue && sensor.getHumidity() > faultyValue);
        for (SensorData sensor : sensorData) {
            if (isAllDataCorrect) {
                StatisticsItem statisticsItem = new StatisticsItem(
                        0, sensor.getId(), sensor.getTemperature(), sensor.getHumidity(), date);
                statisticsViewModel.createStatisticsItem(statisticsItem);
            }
        }
    }

    private void readDataFromDatabaseNotArchivedSortByDate(StatisticsActivity statisticsActivity) {
        Log.i("HygroSense", "start read");
        final Observer<List<StatisticsItem>> statisticsObserver = statisticsItems -> {
            Log.i("HygroSense", "inform listeners, size: " + statisticsItems.size());
            if (statisticsItems.size() > 0) {
                Log.i("HygroSense", "statistics item get 0: " + statisticsItems.get(0).getTemperature());
                for (StatisticsDbEventListener hl : listeners)
                    hl.statisticsDataChanged(statisticsItems);
            }
        };
        statisticsViewModel.getReadAllStatisticsOrderByDate().observe(statisticsActivity, statisticsObserver);
    }

    public void deleteAllFromStatisticsTable() {
        statisticsViewModel.deleteAll();
    }

    public void saveToStatisticsTableTest() {
        Date date = new Date();
        StatisticsItem statisticsItem = new StatisticsItem(
                0, 0, 0, 0, date);
        statisticsViewModel.createStatisticsItem(statisticsItem);
    }

    private List<StatisticsDbEventListener> listeners = new ArrayList<>();

    public void addListener(StatisticsDbEventListener toAdd) {
        listeners.add(toAdd);
    }
}
