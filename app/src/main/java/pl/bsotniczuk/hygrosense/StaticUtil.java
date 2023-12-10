package pl.bsotniczuk.hygrosense;

import pl.bsotniczuk.hygrosense.model.SensorData;

public class StaticUtil {
    public static volatile boolean wasReferenceSensorDataFetched = false;
    public static volatile boolean wasSensorDataFetched = false;
    public static volatile boolean isCalibrationInProgress = false;
    public static volatile boolean isSynchronizeSensorsThreadStarted = false;
    public static volatile SensorData[] sensorData;
    public static volatile SensorData referenceSensorData;

    public static class Constants {
        public static final int referenceSensorId = 99;
        public static final float faultyValueTemperatureMin = -275;
        public static final float faultyValueTemperatureMax = 100;
        public static final float faultyValueHumidityMin = 0;
        public static final float faultyValueHumidityMax = 100;
    }
}
