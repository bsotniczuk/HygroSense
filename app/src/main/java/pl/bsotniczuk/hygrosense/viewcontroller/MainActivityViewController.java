package pl.bsotniczuk.hygrosense.viewcontroller;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.widget.TextView;

import pl.bsotniczuk.hygrosense.ApiFetcher;
import pl.bsotniczuk.hygrosense.HygroEventListener;
import pl.bsotniczuk.hygrosense.MainActivity;
import pl.bsotniczuk.hygrosense.R;
import pl.bsotniczuk.hygrosense.controller.DatabaseController;
import pl.bsotniczuk.hygrosense.controller.ToolbarController;

public class MainActivityViewController implements HygroEventListener {

    MainActivity mainActivity;
    ApiFetcher apiFetcher;
    TextView temperatureValueTextView;
    TextView humidityValueTextView;

    public static DatabaseController databaseController;
    private boolean wasConnectionEstablished;
    public static String ipAddress = "";

    public MainActivityViewController(
            MainActivity mainActivity,
            ApiFetcher apiFetcher,
            TextView temperatureValueTextView,
            TextView humidityValueTextView
            ) {
        this.mainActivity = mainActivity;
        databaseController = new DatabaseController(this.mainActivity);
        this.apiFetcher = apiFetcher;
        this.temperatureValueTextView = temperatureValueTextView;
        this.humidityValueTextView = humidityValueTextView;

        new ToolbarController(this.mainActivity, this.mainActivity.findViewById(R.id.toolbar));

        refreshTextViewThread();
    }

    @Override
    public void hygroDataChanged() {
        Log.i("HygroSense", "hygro data changed");
        String temperatureText = "" + apiFetcher.getTemperature() + " \u2103";
        String humidityText = "" + apiFetcher.getHumidity() + " %";
        temperatureValueTextView.setText(temperatureText);
        humidityValueTextView.setText(humidityText);

        if (!this.wasConnectionEstablished) {
            connectionEstablished();
        }
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
                                apiFetcher.fetchApiDataInfo(ipAddress);
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

    public void connectionEstablished() {
        TextView connectionTextView = mainActivity.findViewById(R.id.connectionTextView);

        ColorDrawable[] array = {
                new ColorDrawable(mainActivity.getResources().getColor(R.color.colorConnecting)),
                new ColorDrawable(mainActivity.getResources().getColor(R.color.colorConnectionEstablished))
        };
        TransitionDrawable colorTransition = new TransitionDrawable(array);
        connectionTextView.setBackground(colorTransition);
        colorTransition.startTransition(1000);
        connectionTextView.setText("");

        this.wasConnectionEstablished = true;
    }
}
