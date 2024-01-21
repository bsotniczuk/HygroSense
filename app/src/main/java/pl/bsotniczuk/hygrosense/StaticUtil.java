package pl.bsotniczuk.hygrosense;

import pl.bsotniczuk.hygrosense.model.SensorData;

public class StaticUtil {
    public static volatile boolean wasReferenceSensorDataFetched = false;
    public static volatile boolean wasSensorDataFetched = false;
    public static volatile boolean isCalibrationInProgress = false;
    public static volatile boolean isSynchronizeSensorsThreadStarted = false;
    public static volatile SensorData[] sensorData;
    public static volatile SensorData referenceSensorData;

    public static class RequestCodes {
        public static final int CAMERA_REQUEST_CODE = 100;
        public static final int CAMERA_REQUEST_CODE_CALIBRATION = 101;
        public static final int CAMERA_REQUEST_CODE_AUTO_CALIBRATION = 102;
        public static final int STORAGE_REQUEST_CODE = 103;
        public static final int OVERLAY_REQUEST_CODE = 104;
    }

    public static class Constants {
        public static final int referenceSensorId = 99;
        public static final String referenceSensorName = "UT333BT";
        public static final float faultyValueTemperatureMin = -274;
        public static final float faultyValueTemperatureMax = 100;
        public static final float faultyValueHumidityMin = 0;
        public static final float faultyValueHumidityMax = 100;
    }
}
