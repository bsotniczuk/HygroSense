package pl.bsotniczuk.hygrosense;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pl.bsotniczuk.hygrosense.api.SensorDataApi;
import pl.bsotniczuk.hygrosense.model.SensorData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFetcher {

    private Float temperature = 0f;
    private Float humidity = 0f;
    private List<HygroEventListener> listeners = new ArrayList<HygroEventListener>();

    public void addListener(HygroEventListener hygroEventListener) {
        listeners.add(hygroEventListener);
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    public void fetchApiDataInfo(String ipAddress) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ipAddress)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SensorDataApi sensorDataApi = retrofit.create(SensorDataApi.class);
        Call<SensorData> call = sensorDataApi.getSensorData();

        call.enqueue(new Callback<SensorData>() {
            @Override
            public void onResponse(Call<SensorData> call, Response<SensorData> response) {
                if (response.code() == 200) {
                    setTemperature(response.body().getTemperature());
                    setHumidity(response.body().getHumidity());
                    String deviceName = response.body().getDeviceName();

                    SensorData sensorData = response.body();

                    Log.i("HygroSense", "Data fetched, temp: " + getTemperature() +
                            " | humidity: " + getHumidity() + " | deviceName: " + deviceName);

                    for (HygroEventListener hl : listeners)
                        hl.hygroDataChanged(sensorData);
                }
            }

            @Override
            public void onFailure(Call<SensorData> call, Throwable t) {
                Log.i("HygroSense", "Data call to API failed: " + t);
            }
        });
    }
}
