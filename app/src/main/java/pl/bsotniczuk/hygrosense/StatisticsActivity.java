package pl.bsotniczuk.hygrosense;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.ScatterChart;

import java.text.DecimalFormat;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import pl.bsotniczuk.hygrosense.chart.StatisticsScatterChart;
import pl.bsotniczuk.hygrosense.controller.StatisticsDbController;
import pl.bsotniczuk.hygrosense.controller.ToolbarStatisticsController;
import pl.bsotniczuk.hygrosense.data.model.StatisticsItem;

public class StatisticsActivity extends AppCompatActivity implements StatisticsDbEventListener {

    private StatisticsDbController statisticsDbController;
    private TextView temperatureAverageTextView;
    private TextView humidityAverageTextView;
    private TextView temperatureMaxTextView;
    private TextView humidityMaxTextView;
    private TextView recordCountTextView;

    private ScatterChart chartTemperature;
    private ScatterChart chartHumidity;
    private StatisticsScatterChart statisticsScatterChart;

    private static final DecimalFormat df = new DecimalFormat("0.0");

    private boolean wasChartDrawn;

    private float temperatureSum;
    private float humiditySum;
    private float temperatureMin = Float.MAX_VALUE;
    private float humidityMin = Float.MAX_VALUE;
    private float temperatureMax = Float.MIN_VALUE;
    private float humidityMax = Float.MIN_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        new ToolbarStatisticsController(this, this.findViewById(R.id.toolbar));

        temperatureAverageTextView = findViewById(R.id.temperatureAverageTextView);
        humidityAverageTextView = findViewById(R.id.humidityAverageTextView);
        temperatureMaxTextView = findViewById(R.id.temperatureMaxTextView);
        humidityMaxTextView = findViewById(R.id.humidityMaxTextView);
        recordCountTextView = findViewById(R.id.recordCountTextView);

        statisticsDbController = new StatisticsDbController(this);

        chartTemperature = findViewById(R.id.chartTemperature);
        chartHumidity = findViewById(R.id.chartHumidity);
        statisticsScatterChart = new StatisticsScatterChart();
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void statisticsDataChanged(List<StatisticsItem> statisticsItems) {
        Log.i("HygroSense", "statistics activity informed");
        temperatureSum = humiditySum = 0f;
        for (StatisticsItem statisticsItem : statisticsItems) {
            temperatureSum += statisticsItem.getTemperature();
            humiditySum += statisticsItem.getHumidity();
            setMinMaxValues(statisticsItem);
        }
        Log.i("HygroSense", "temp sum: \n" + temperatureSum);
        Log.i("HygroSense", "humidity sum: \n" + humiditySum);

        this.runOnUiThread(() -> {
            temperatureAverageTextView.setText(df.format(temperatureSum / statisticsItems.size()) + " \u2103");
            humidityAverageTextView.setText(df.format(humiditySum / statisticsItems.size()) + " %");
            recordCountTextView.setText("(" + statisticsItems.size() + " " + getString(R.string.records) + ")");
            temperatureMaxTextView.setText(df.format(temperatureMin) + " \u2103" + " / " + df.format(temperatureMax) + " \u2103");
            humidityMaxTextView.setText(df.format(humidityMin) + " %" + " / " + df.format(humidityMax) + " %");
        });

        if (!wasChartDrawn) {
            statisticsScatterChart.drawCharts(statisticsItems, chartTemperature, chartHumidity);
            wasChartDrawn = true;
        }
    }

    private void setMinMaxValues(StatisticsItem statisticsItem) {
        if (statisticsItem.getHumidity() > humidityMax) {
            humidityMax = statisticsItem.getHumidity();
        }
        if (statisticsItem.getHumidity() < humidityMin) {
            humidityMin = statisticsItem.getHumidity();
        }
        if (statisticsItem.getTemperature() > temperatureMax) {
            temperatureMax = statisticsItem.getTemperature();
        }
        if (statisticsItem.getTemperature() < temperatureMin) {
            temperatureMin = statisticsItem.getTemperature();
        }
    }
}