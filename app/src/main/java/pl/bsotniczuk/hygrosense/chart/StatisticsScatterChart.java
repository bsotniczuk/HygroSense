package pl.bsotniczuk.hygrosense.chart;

import android.util.Log;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import pl.bsotniczuk.hygrosense.data.model.StatisticsItem;

public class StatisticsScatterChart {

    public void drawCharts(List<StatisticsItem> statisticsItems, ScatterChart chartTemperature, ScatterChart chartHumidity) {
        chartTemperature = setChartSettings(chartTemperature, true);
        chartHumidity = setChartSettings(chartHumidity, false);

        Map<Integer, ArrayList<Entry>> sensorsAndEntries = new HashMap<>();
        Map<Integer, ArrayList<Entry>> sensorsAndEntriesHumidity = new HashMap<>();

        for (StatisticsItem statisticsItem : statisticsItems) {
            if (!sensorsAndEntries.containsKey(statisticsItem.getSensor_id())) {
                sensorsAndEntries.put(statisticsItem.getSensor_id(), new ArrayList<>());
                sensorsAndEntriesHumidity.put(statisticsItem.getSensor_id(), new ArrayList<>());
                Log.i("HygroSense", "key: " + statisticsItem.getSensor_id());
            }
        }

        for (StatisticsItem statisticsItem : statisticsItems) {
            for (Integer sensorAndEntry : sensorsAndEntries.keySet()) {
                if (sensorAndEntry == statisticsItem.getSensor_id()) {
                    sensorsAndEntries.get(sensorAndEntry).add(
                            new Entry(statisticsItem.getDate().getTime(), statisticsItem.getTemperature()));
                    sensorsAndEntriesHumidity.get(sensorAndEntry).add(
                            new Entry(statisticsItem.getDate().getTime(), statisticsItem.getHumidity()));
                }
            }
        }

        ArrayList<IScatterDataSet> iScatterDataSets = new ArrayList<>();
        ArrayList<IScatterDataSet> iScatterDataSetsHumidity = new ArrayList<>();
        for (Integer sensorId : sensorsAndEntries.keySet()) {
            ScatterDataSet scatterDataSet = getScatterDataSet(sensorsAndEntries, sensorId);
            ScatterDataSet scatterDataSetHumidity = getScatterDataSet(sensorsAndEntriesHumidity, sensorId);
            iScatterDataSets.add(scatterDataSet);
            iScatterDataSetsHumidity.add(scatterDataSetHumidity);
        }

        ScatterData data = new ScatterData(iScatterDataSets);
        ScatterData dataHumidity = new ScatterData(iScatterDataSetsHumidity);
        setChartData(chartTemperature, data);
        setChartData(chartHumidity, dataHumidity);
    }

    private void setChartData(ScatterChart chart, ScatterData data) {
        chart.setData(data);
        chart.invalidate();
    }

    private ScatterChart setChartSettings(ScatterChart chart, boolean isTemperature) {
        // below line is use to disable the description
        // of our scatter chart.
        chart.getDescription().setEnabled(false);

        // below line is use to draw grid background
        // and we are setting it to false.
        chart.setDrawGridBackground(false);

        // below line is use to set touch
        // enable for our chart.
        chart.setTouchEnabled(true);

        // below line is use to set maximum
        // highlight distance for our chart.
        chart.setMaxHighlightDistance(50f);

        // below line is use to set
        // dragging for our chart.
        chart.setDragEnabled(true);

        // below line is use to set scale
        // to our chart.
        chart.setScaleEnabled(true);

        // below line is use to set maximum
        // visible count to our chart.
        chart.setMaxVisibleValueCount(200);

        // below line is use to set
        // pinch zoom to our chart.
        chart.setPinchZoom(false);

        // below line we are getting
        // the legend of our chart.
        Legend l = chart.getLegend();

        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        // below line is use for
        // setting draw inside to false.
        l.setDrawInside(false);

        // below line is use to set
        // offset value for our legend.
        l.setXOffset(5f);

        YAxis yl = chart.getAxisLeft();
//        yl.setAxisMinimum(0f);

        if (isTemperature) {
            TemperatureYAxisValueFormatter temperatureYAxisValueFormatter = new TemperatureYAxisValueFormatter();
            yl.setValueFormatter(temperatureYAxisValueFormatter);
        }
        else {
            HumidityYAxisValueFormatter humidityYAxisValueFormatter = new HumidityYAxisValueFormatter();
            yl.setValueFormatter(humidityYAxisValueFormatter);
        }

        // below line is use to get axis
        // right of our chart
        chart.getAxisRight().setEnabled(false);

        XAxis xl = chart.getXAxis();

        // below line is use to enable
        // drawing of grid lines.
        xl.setDrawGridLines(false);

        DateXAxisValueFormatter dateXAxisValueFormatter = new DateXAxisValueFormatter();
        xl.setValueFormatter(dateXAxisValueFormatter);

        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        xl.setPosition(position);
        xl.setLabelRotationAngle(315f);
        xl.setCenterAxisLabels(true);

        xl.setDrawLimitLinesBehindData(true); //TODO: what is it?

        return chart;
    }

    @NonNull
    private ScatterDataSet getScatterDataSet(Map<Integer, ArrayList<Entry>> sensorsAndEntries, Integer sensorAndEntry) {
        ScatterDataSet scatterDataSet = new ScatterDataSet(sensorsAndEntries.get(sensorAndEntry), "DHT22 id: " + sensorAndEntry);
        scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        scatterDataSet.setScatterShapeSize(12f);
        if (sensorAndEntry < ColorTemplate.COLORFUL_COLORS.length) {
            scatterDataSet.setColor(ColorTemplate.COLORFUL_COLORS[sensorAndEntry]);
        }
        else {
            scatterDataSet.setColor(ColorTemplate.PASTEL_COLORS[0]);
        }
        return scatterDataSet;
    }
}
