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

    private List<HygroEventListener> listeners = new ArrayList<HygroEventListener>();

    public void addListener(HygroEventListener hygroEventListener) {
        listeners.add(hygroEventListener);
    }

    public void fetchApiDataInfo(String ipAddress) {
        fetchApiDataInfo(ipAddress, false);
    }

    public void fetchApiDataInfoAutoCalibration(String ipAddress) {
        fetchApiDataInfo(ipAddress, true);
    }

    private void fetchApiDataInfo(String ipAddress, boolean isAutoCalibration) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ipAddress)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SensorDataApi sensorDataApi = retrofit.create(SensorDataApi.class);
        Call<SensorData[]> call = sensorDataApi.getSensorData();

        call.enqueue(new Callback<SensorData[]>() {
            @Override
            public void onResponse(Call<SensorData[]> call, Response<SensorData[]> response) {
                if (response.code() == 200) {
                    Log.i("HygroSense", "response body: \n" + response.body()[0].getId());
                    SensorData[] sensorData = response.body();

                    for (SensorData sensor : sensorData) {
                        Log.i("HygroSense", "Data fetched, id: " + sensor.getId() + " temp: " + sensor.getTemperature() +
                                " | humidity: " + sensor.getHumidity() + " | deviceName: " + sensor.getDeviceName());
                    }
                    if (!isAutoCalibration) {
                        for (HygroEventListener hl : listeners)
                            hl.hygroDataChanged(sensorData);
                    }
                    else {
                        for (HygroEventListener hl : listeners)
                            hl.hygroDataChangedAutoCalibrationSensors(sensorData);
                    }
                }
            }

            @Override
            public void onFailure(Call<SensorData[]> call, Throwable t) {
                Log.i("HygroSense", "Data call to API failed: " + t);
            }
        });
    }
}
