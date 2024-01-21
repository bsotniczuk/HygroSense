package pl.bsotniczuk.hygrosense.calibration;

public class CalibrationParams {
    private int id;
    private CalibrationParameters calibrationParametersTemperature;
    private CalibrationParameters calibrationParametersHumidity;

    public CalibrationParams(int id, CalibrationParameters calibrationParametersTemperature, CalibrationParameters calibrationParametersHumidity) {
        this.id = id;
        this.calibrationParametersTemperature = calibrationParametersTemperature;
        this.calibrationParametersHumidity = calibrationParametersHumidity;
    }

    public int getId() {
        return id;
    }

    public CalibrationParameters getCalibrationParametersTemperature() {
        return calibrationParametersTemperature;
    }

    public CalibrationParameters getCalibrationParametersHumidity() {
        return calibrationParametersHumidity;
    }
}
