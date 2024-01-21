package pl.bsotniczuk.hygrosense.viewcontroller;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import pl.bsotniczuk.hygrosense.ApiFetcher;
import pl.bsotniczuk.hygrosense.ConnectionType;
import pl.bsotniczuk.hygrosense.HygroEventListener;
import pl.bsotniczuk.hygrosense.MainActivity;
import pl.bsotniczuk.hygrosense.calibration.PhotoTaker;
import pl.bsotniczuk.hygrosense.R;
import pl.bsotniczuk.hygrosense.StaticUtil;
import pl.bsotniczuk.hygrosense.calibration.CameraTextRecognition;
import pl.bsotniczuk.hygrosense.controller.DatabaseController;
import pl.bsotniczuk.hygrosense.controller.StatisticsDbController;
import pl.bsotniczuk.hygrosense.controller.ToolbarMainActivityController;
import pl.bsotniczuk.hygrosense.model.SensorData;

public class MainActivityViewController implements HygroEventListener {

    private MainActivity mainActivity;
    private ApiFetcher apiFetcher;
    private TextView temperatureValueTextView;
    private TextView humidityValueTextView;
    private TextView connectionTextView;
    private StatisticsDbController statisticsDbController;

    public static DatabaseController databaseController;
    private boolean wasConnectionEstablished;
    private boolean wasTemperatureOrHumidityValueTextViewClicked;

    private String temperatureText;
    private String humidityText;
    private String temperatureTextAllSensors;
    private String humidityTextAllSensors;

    private CameraTextRecognition cameraTextRecognition;

    private int apiFetcherFrequency; //cannot be lower than 2s -> 2000ms
    private int apiFetcherFrequencyStandard = 3000; //cannot be lower than 2s -> 2000ms
    private int apiFetcherFrequencyCalibration = 10000; //cannot be lower than 2s -> 2000ms

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public MainActivityViewController(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.temperatureValueTextView = this.mainActivity.findViewById(R.id.temperatureValueTextView);
        this.humidityValueTextView = this.mainActivity.findViewById(R.id.humidityValueTextView);
        this.connectionTextView = this.mainActivity.findViewById(R.id.connectionTextView);
        databaseController = new DatabaseController(this.mainActivity);
        databaseController.initDbAndSetupAwsIotCoreConnection(this);
        apiFetcherFrequency = apiFetcherFrequencyStandard;
        this.apiFetcher = new ApiFetcher();
        this.apiFetcher.addListener(this);
        this.statisticsDbController = new StatisticsDbController(this.mainActivity);

        new ToolbarMainActivityController(this.mainActivity, this.mainActivity.findViewById(R.id.toolbar), this);

        temperatureValueTextView.setOnClickListener(v -> temperatureOrHumidityValueTextViewClick());
        humidityValueTextView.setOnClickListener(v -> temperatureOrHumidityValueTextViewClick());

        fetchSensorDataThread();
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
        displayAndSaveSensorData(sensorData);
    }

    @Override
    public void hygroDataChangedAutoCalibrationSensors(SensorData[] sensorData) {
        Log.i("HygroSense", "AutoCalibrationSensors data changed");
        StaticUtil.wasSensorDataFetched = true;
        StaticUtil.sensorData = sensorData;
    }

    public void hygroDataChangedAutoCalibrationReferenceSensor(SensorData sensorData) {
        Log.i("HygroSense", "ReferenceSensor data changed");
        StaticUtil.wasReferenceSensorDataFetched = true;
        StaticUtil.referenceSensorData = sensorData;
    }

    private void displayAndSaveSensorData(SensorData[] sensorData) {
        if (!this.wasConnectionEstablished) {
            connectionEstablished();
        } else if (connectionTextView.getText().length() > 0) {
            mainActivity.runOnUiThread(() -> connectionTextView.setText(""));
        }
        if (!isFaultySensorData(sensorData)) {
            statisticsDbController.saveToStatisticsTable(sensorData);
            setStringsToDisplay(sensorData);
            setTemperatureAndHumidityValueTextViews();
        }
    }

    private boolean isFaultySensorData(SensorData[] sensorData) {
        return Arrays.stream(sensorData).allMatch(sensor -> isFaultySensorData(sensor));
    }

    private boolean isFaultySensorData(SensorData sensor) {
        return sensor.getHumidity() < StaticUtil.Constants.faultyValueHumidityMin
                || sensor.getHumidity() > StaticUtil.Constants.faultyValueHumidityMax
                || sensor.getTemperature() < StaticUtil.Constants.faultyValueTemperatureMin
                || sensor.getTemperature() > StaticUtil.Constants.faultyValueTemperatureMax;
    }

    private void setStringsToDisplay(SensorData[] sensorData) {
        humidityTextAllSensors = temperatureTextAllSensors = mainActivity.getString(R.string.all_sensors) + "\n";
        float temperatureSum = 0;
        float humiditySum = 0;
        int i = 0;

        for (SensorData sensor : sensorData) {
            if (sensor.getHumidity() >= StaticUtil.Constants.faultyValueHumidityMin
                    && sensor.getTemperature() > StaticUtil.Constants.faultyValueTemperatureMin) {
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

    public void fetchSensorDataThread() {
        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(apiFetcherFrequency);  //1000ms = 1 sec
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("HygroSense", "fetchSensorDataThread started");
                                if (DatabaseController.settingsItem.getConnection_type() == ConnectionType.ACCESS_POINT) {
                                    if (StaticUtil.isCalibrationInProgress && !StaticUtil.isSynchronizeSensorsThreadStarted) {
                                        apiFetcherFrequency = apiFetcherFrequencyCalibration;
                                        new PhotoTaker(mainActivity); //TODO: change the api fetcher to also save data from photos to db
                                        apiFetcher.fetchApiDataInfoAutoCalibration(DatabaseController.settingsItem.getEsp32_ip_address_access_point());
                                        synchronizeSensorsDataThread();
                                    } else if (!StaticUtil.isSynchronizeSensorsThreadStarted) {
                                        apiFetcherFrequency = apiFetcherFrequencyStandard;
                                        apiFetcher.fetchApiDataInfo(DatabaseController.settingsItem.getEsp32_ip_address_access_point());
                                    }
                                } else {
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

    private AtomicInteger tries = new AtomicInteger();

    public void synchronizeSensorsDataThread() {
        Log.i("HygroSense", "synchronizeSensorsDataThread started");
        StaticUtil.isSynchronizeSensorsThreadStarted = true;
        Thread t = new Thread() {
            @Override
            public void run() {
                while (StaticUtil.isSynchronizeSensorsThreadStarted) {
                    try {
                        Thread.sleep(400);  //1000ms = 1 sec
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("HygroSense", "synchronizeSensorsDataThread run: " + tries.get());

                                tries.getAndIncrement();
                                if ((StaticUtil.wasSensorDataFetched && StaticUtil.wasReferenceSensorDataFetched)
                                        || tries.get() >= 8) {
                                    if (tries.get() >= 8) Log.i("HygroSense", "synchronizeSensorsDataThread failed to sync");
                                    else Log.i("HygroSense", "synchronizeSensorsDataThread data synchronized");
                                    StaticUtil.wasSensorDataFetched = StaticUtil.wasReferenceSensorDataFetched = false;
                                    StaticUtil.isSynchronizeSensorsThreadStarted = false;
                                    tries.set(0);

                                    if (StaticUtil.sensorData != null)
                                        for (SensorData sensor : StaticUtil.sensorData)
                                            Log.i("HygroSense", "synchronizeSensorsDataThread id: " + sensor.getId() + " | temperature: " + sensor.getTemperature() + " | humidity: " + sensor.getHumidity());

                                    if (StaticUtil.referenceSensorData != null)
                                        Log.i("HygroSense", "synchronizeSensorsDataThread id: " + StaticUtil.referenceSensorData.getId() + " | temperature: " + StaticUtil.referenceSensorData.getTemperature() + " | humidity: " + StaticUtil.referenceSensorData.getHumidity());

                                    SensorData[] sensorData;
                                    if (StaticUtil.sensorData != null && StaticUtil.sensorData.length > 0 && StaticUtil.referenceSensorData != null) {
                                        int sizeOfArray = StaticUtil.sensorData.length + 1;
                                        sensorData = new SensorData[sizeOfArray];
                                        sensorData[sizeOfArray - 1] = StaticUtil.referenceSensorData;
                                        for (int i = 0; i < sizeOfArray - 1; i++) {
                                            sensorData[i] = StaticUtil.sensorData[i];
                                        }
                                        Log.i("HygroSense", "synchronizeSensorsDataThread length StaticUtil.sensorData: "
                                                + StaticUtil.sensorData.length);

                                        for (SensorData sensor : sensorData) {
                                            Log.i("HygroSense", "extra synchronizeSensorsDataThread id: "
                                                    + sensor.getId()
                                                    + " | temperature: " + sensor.getTemperature()
                                                    + " | humidity: " + sensor.getHumidity());
                                        }
                                        Log.i("HygroSense", "synchronizeSensorsDataThread length sensorData: "
                                                + sensorData.length);
                                        hygroDataChanged(sensorData);
                                    }
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

    public void openCameraTextRecognitionMenu() {
        cameraTextRecognition = new CameraTextRecognition(mainActivity);
        cameraTextRecognition.showInputImageDialog();
    }

    public void processImage() {
        cameraTextRecognition.processImage();
    }

    public void processImage(Uri imageUri) {
        cameraTextRecognition.processImage(imageUri);
    }

    public void permissionRelated(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        cameraTextRecognition.permissionRelated(requestCode, permissions, grantResults);
    }
}
