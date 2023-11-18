package pl.bsotniczuk.hygrosense.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class TemperatureYAxisValueFormatter extends ValueFormatter {

    private static final DecimalFormat df = new DecimalFormat("0.0");

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return df.format(value) + " \u2103";
    }
}