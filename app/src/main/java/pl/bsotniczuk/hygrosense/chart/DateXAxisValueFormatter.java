package pl.bsotniczuk.hygrosense.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateXAxisValueFormatter extends ValueFormatter {

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        Float dateFloat = value;
        long dateLong = dateFloat.longValue();
        Date date = new Date(dateLong);

        String toReturn = new SimpleDateFormat("dd.MM.yy").format(date);
        toReturn += " ";
        toReturn += new SimpleDateFormat("HH:mm").format(date);
        return toReturn;
    }
}