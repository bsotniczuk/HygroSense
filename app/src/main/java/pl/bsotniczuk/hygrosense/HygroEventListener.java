package pl.bsotniczuk.hygrosense;

import pl.bsotniczuk.hygrosense.model.SensorData;

//Observer Pattern
public interface HygroEventListener {
    void hygroDataChanged(SensorData[] sensorData);
    void hygroDataChangedAutoCalibrationSensors(SensorData[] sensorData);
}
