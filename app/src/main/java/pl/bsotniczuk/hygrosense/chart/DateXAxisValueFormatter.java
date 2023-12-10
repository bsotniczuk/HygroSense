package pl.bsotniczuk.hygrosense.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pl.bsotniczuk.hygrosense.data.model.StatisticsItem;

public class DateXAxisValueFormatter extends ValueFormatter {

    private List<StatisticsItem> statisticsItems;

    public DateXAxisValueFormatter(List<StatisticsItem> statisticsItems) {
        this.statisticsItems = statisticsItems;
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        int index = Math.round(value);
        if (index < 0 || index >= statisticsItems.size()) return "";
        Date date = statisticsItems.get(index).getDate();

        return new SimpleDateFormat("dd.MM HH:mm").format(date);
    }
}