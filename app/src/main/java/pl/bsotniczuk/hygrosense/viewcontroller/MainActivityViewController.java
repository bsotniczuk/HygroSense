package pl.bsotniczuk.hygrosense.viewcontroller;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.widget.TextView;

import pl.bsotniczuk.hygrosense.ApiFetcher;
import pl.bsotniczuk.hygrosense.ConnectionType;
import pl.bsotniczuk.hygrosense.HygroEventListener;
import pl.bsotniczuk.hygrosense.MainActivity;
import pl.bsotniczuk.hygrosense.R;
import pl.bsotniczuk.hygrosense.controller.DatabaseController;
import pl.bsotniczuk.hygrosense.controller.ToolbarMainActivityController;
import pl.bsotniczuk.hygrosense.model.SensorData;

public class MainActivityViewController implements HygroEventListener {

    MainActivity mainActivity;
    ApiFetcher apiFetcher;
    TextView temperatureValueTextView;
    TextView humidityValueTextView;

    public static DatabaseController databaseController;
    private boolean wasConnectionEstablished;

    public MainActivityViewController(
            MainActivity mainActivity
            ) {
        this.mainActivity = mainActivity;
        this.temperatureValueTextView = this.mainActivity.findViewById(R.id.temperatureValueTextView);
        this.humidityValueTextView = this.mainActivity.findViewById(R.id.humidityValueTextView);
        databaseController = new DatabaseController(this.mainActivity);
        databaseController.initDbAndSetupAwsIotCoreConnection(this);
        this.apiFetcher = new ApiFetcher();
        this.apiFetcher.addListener(this);

        new ToolbarMainActivityController(this.mainActivity, this.mainActivity.findViewById(R.id.toolbar));

        refreshTextViewThread();
    }

    @Override
    public void hygroDataChanged(SensorData sensorData) {
        Log.i("HygroSense", "hygro data changed");
        Log.i("HygroSense", "sensorData temperature: " + sensorData.getTemperature());
        Log.i("HygroSense", "sensorData humidity: " + sensorData.getHumidity());
        String temperatureText = "" + apiFetcher.getTemperature() + " \u2103";
        String humidityText = "" + apiFetcher.getHumidity() + " %";
        temperatureValueTextView.setText(temperatureText);
        humidityValueTextView.setText(humidityText);

        if (!this.wasConnectionEstablished) {
            connectionEstablished();
        }
    }

    @Override
    public void hygroDataChangedAws(SensorData sensorData) {
        Log.i("HygroSense", "AWS hygro data changed");
        String temperatureText = "" + sensorData.getTemperature() + " \u2103";
        String humidityText = "" + sensorData.getHumidity() + " %";
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
                            if (DatabaseController.settingsItem.getConnection_type() == ConnectionType.ACCESS_POINT) {
                                apiFetcher.fetchApiDataInfo(DatabaseController.settingsItem.getEsp32_ip_address_access_point());
                            }
                            else {
                                interrupt();
                            }
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
