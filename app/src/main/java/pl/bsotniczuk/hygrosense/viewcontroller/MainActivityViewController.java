package pl.bsotniczuk.hygrosense.viewcontroller;

import android.util.Log;
import android.widget.TextView;

import pl.bsotniczuk.hygrosense.ApiFetcher;
import pl.bsotniczuk.hygrosense.HygroEventListener;
import pl.bsotniczuk.hygrosense.MainActivity;

public class MainActivityViewController implements HygroEventListener {

    MainActivity mainActivity;
    ApiFetcher apiFetcher;
    TextView temperatureValueTextView;
    TextView humidityValueTextView;

    public MainActivityViewController(MainActivity mainActivity, ApiFetcher apiFetcher, TextView temperatureValueTextView, TextView humidityValueTextView) {
        this.mainActivity = mainActivity;
        this.apiFetcher = apiFetcher;
        this.temperatureValueTextView = temperatureValueTextView;
        this.humidityValueTextView = humidityValueTextView;

        refreshTextViewThread();
    }

    @Override
    public void hygroDataChanged() {
        Log.i("HygroSense", "hygro data changed");
        temperatureValueTextView.setText("" + apiFetcher.getTemperature() + " \u2103");
        humidityValueTextView.setText("" + apiFetcher.getHumidity() + " %");
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
