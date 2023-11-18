package pl.bsotniczuk.hygrosense.viewcontroller;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.widget.TextView;

import java.text.DecimalFormat;

import pl.bsotniczuk.hygrosense.ApiFetcher;
import pl.bsotniczuk.hygrosense.ConnectionType;
import pl.bsotniczuk.hygrosense.HygroEventListener;
import pl.bsotniczuk.hygrosense.MainActivity;
import pl.bsotniczuk.hygrosense.R;
import pl.bsotniczuk.hygrosense.controller.DatabaseController;
import pl.bsotniczuk.hygrosense.controller.StatisticsDbController;
import pl.bsotniczuk.hygrosense.controller.ToolbarMainActivityController;
import pl.bsotniczuk.hygrosense.model.SensorData;

public class MainActivityViewController implements HygroEventListener {

    MainActivity mainActivity;
    ApiFetcher apiFetcher;
    TextView temperatureValueTextView;
    TextView humidityValueTextView;
    TextView connectionTextView;
    StatisticsDbController statisticsDbController;

    public static final float faultyValue = -275;

    public static DatabaseController databaseController;
    private boolean wasConnectionEstablished;
    private boolean wasTemperatureOrHumidityValueTextViewClicked;

    private String temperatureText;
    private String humidityText;
    private String temperatureTextAllSensors;
    private String humidityTextAllSensors;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public MainActivityViewController(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.temperatureValueTextView = this.mainActivity.findViewById(R.id.temperatureValueTextView);
        this.humidityValueTextView = this.mainActivity.findViewById(R.id.humidityValueTextView);
        this.connectionTextView = this.mainActivity.findViewById(R.id.connectionTextView);
        databaseController = new DatabaseController(this.mainActivity);
        databaseController.initDbAndSetupAwsIotCoreConnection(this);
        this.apiFetcher = new ApiFetcher();
        this.apiFetcher.addListener(this);
        this.statisticsDbController = new StatisticsDbController(this.mainActivity);

        new ToolbarMainActivityController(this.mainActivity, this.mainActivity.findViewById(R.id.toolbar));

        temperatureValueTextView.setOnClickListener(v -> temperatureOrHumidityValueTextViewClick());
        humidityValueTextView.setOnClickListener(v -> temperatureOrHumidityValueTextViewClick());

        refreshTextViewThread();
    }

    private void temperatureOrHumidityValueTextViewClick() {
        wasTemperatureOrHumidityValueTextViewClicked = !wasTemperatureOrHumidityValueTextViewClicked;
        setTemperatureAndHumidityValueTextViews();
    }

    private void setTemperatureAndHumidityValueTextViews() {
        if (this.wasConnectionEstablished) {
            if (!wasTemperatureOrHumidityValueTextViewClicked) {
                mainActivity.runOnUiThread(() -> {
                    temperatureValueTextView.setText(temperatureText);
                    humidityValueTextView.setText(humidityText);
                });
            } else {
                mainActivity.runOnUiThread(() -> {
                    temperatureValueTextView.setText(temperatureTextAllSensors);
                    humidityValueTextView.setText(humidityTextAllSensors);
                });
            }
        }
    }

    @Override
    public void hygroDataChanged(SensorData[] sensorData) {
        Log.i("HygroSense", "AWS Mode: hygro data changed");
        displaySensorDataMultipleSensors(sensorData);
    }

    private void displaySensorDataMultipleSensors(SensorData[] sensorData) {
        if (!this.wasConnectionEstablished) {
            connectionEstablished();
        }
        else if (connectionTextView.getText().length() > 0) {
            mainActivity.runOnUiThread(() -> connectionTextView.setText(""));
        }
        if (!isFaultySensorData(sensorData)) {
            statisticsDbController.saveToStatisticsTable(sensorData);
            setStringsToDisplay(sensorData);
            setTemperatureAndHumidityValueTextViews();
        }
    }

    private boolean isFaultySensorData(SensorData[] sensorData) {
        int faultyDataCounter = 0;
        for (SensorData sensor : sensorData) {
            if (sensor.getHumidity() < faultyValue || sensor.getTemperature() < faultyValue) {
                faultyDataCounter++;
            }
        }
        return faultyDataCounter == sensorData.length;
    }

    private void setStringsToDisplay(SensorData[] sensorData) {
        humidityTextAllSensors = temperatureTextAllSensors = mainActivity.getString(R.string.all_sensors) + "\n";
        float temperatureSum = 0;
        float humiditySum = 0;
        int i = 0;

        for (SensorData sensor : sensorData) {
            if (sensor.getHumidity() > faultyValue && sensor.getTemperature() > faultyValue) {
                setAllSensorDataStrings(sensor.getId(), df.format(sensor.getTemperature()), df.format(sensor.getHumidity()));
                temperatureSum += sensor.getTemperature();
                humiditySum += sensor.getHumidity();
                i++;
            } else {
                setAllSensorDataStrings(sensor.getId(), "?", "?");
            }
        }
        setAverageSensorDataStrings(temperatureSum / i, humiditySum / i);
    }

    private void setAverageSensorDataStrings(float temperatureAvg, float humidityAvg) {
        temperatureText = mainActivity.getString(R.string.average) + "\n" + df.format(temperatureAvg) + " \u2103";
        humidityText = mainActivity.getString(R.string.average) + "\n" + df.format(humidityAvg) + " %";
    }

    private void setAllSensorDataStrings(int id, String temperature, String humidity) {
        temperatureTextAllSensors += id + " : " + temperature + " \u2103\n";
        humidityTextAllSensors += id + " : " + humidity + " %\n";
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

    private void connectionEstablished() {
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
