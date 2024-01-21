package pl.bsotniczuk.hygrosense.calibration;

public class CalibrationParameters {
    private float d = 0f;
    private float c = 0f;
    private float b = 0f;
    private float a = 0f;

    public CalibrationParameters(float a, float b, float c, float d) {
        this.d = d;
        this.c = c;
        this.b = b;
        this.a = a;
    }

    public CalibrationParameters(float a, float b, float c) {
        this.c = c;
        this.b = b;
        this.a = a;
    }

    public CalibrationParameters(float a, float b) {
        this.b = b;
        this.a = a;
    }

    public CalibrationParameters(float a) {
        this.a = a;
    }

    public float multiplyValueUsingParameters(float x) {
        Double abc = d * Math.pow(x, 3) + c * Math.pow(x, 2) + b * x + a;
        return abc.floatValue();
    }

    public float getD() {
        return d;
    }

    public void setD(float d) {
        this.d = d;
    }

    public float getC() {
        return c;
    }

    public void setC(float c) {
        this.c = c;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    public float getA() {
        return a;
    }

    public void setA(float a) {
        this.a = a;
    }
}
