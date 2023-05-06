package pl.bsotniczuk.hygrosense.viewcontroller;

import android.util.Log;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import pl.bsotniczuk.hygrosense.ApiFetcher;
import pl.bsotniczuk.hygrosense.HygroEventListener;
import pl.bsotniczuk.hygrosense.MainActivity;
import pl.bsotniczuk.hygrosense.R;
import pl.bsotniczuk.hygrosense.controller.DatabaseController;
import pl.bsotniczuk.hygrosense.controller.ToolbarController;
import pl.bsotniczuk.hygrosense.data.viewmodel.SettingsViewModel;

public class MainActivityViewController implements HygroEventListener {

    MainActivity mainActivity;
    ApiFetcher apiFetcher;
    TextView temperatureValueTextView;
    TextView humidityValueTextView;

    private SettingsViewModel settingsViewModel;
    private DatabaseController databaseController;

    public MainActivityViewController(
            MainActivity mainActivity,
            ApiFetcher apiFetcher,
            TextView temperatureValueTextView,
            TextView humidityValueTextView,
            SettingsViewModel settingsViewModel) {
        this.mainActivity = mainActivity;
        this.apiFetcher = apiFetcher;
        this.temperatureValueTextView = temperatureValueTextView;
        this.humidityValueTextView = humidityValueTextView;
        this.settingsViewModel = settingsViewModel;

//        settingsViewModel = new ViewModelProvider(this.mainActivity).get(SettingsViewModel.class);
//        databaseController = new DatabaseController(this.mainActivity);

        new ToolbarController(mainActivity);

        refreshTextViewThread();
    }

    @Override
    public void hygroDataChanged() {
        Log.i("HygroSense", "hygro data changed");
        String temperatureText = "" + apiFetcher.getTemperature() + " \u2103";
        String humidityText = "" + apiFetcher.getHumidity() + " %";
        temperatureValueTextView.setText(temperatureText);
        humidityValueTextView.setText(humidityText);
    }

    public void refreshTextViewThread() {
        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(3000);  //1000ms = 1 sec
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                apiFetcher.fetchApiDataInfo();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }
}
