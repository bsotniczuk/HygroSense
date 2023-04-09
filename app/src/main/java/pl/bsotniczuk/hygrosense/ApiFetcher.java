package pl.bsotniczuk.hygrosense;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import pl.bsotniczuk.hygrosense.api.HumidityApi;
import pl.bsotniczuk.hygrosense.api.SensorDataApi;
import pl.bsotniczuk.hygrosense.model.SensorData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFetcher {

    private static Float temperature = 0f;
    private static Float humidity = 0f;

    public static Float getTemperature() {
        return temperature;
    }

    public static void setTemperature(Float temperature) {
        ApiFetcher.temperature = temperature;
    }

    public static Float getHumidity() {
        return humidity;
    }

    public static void setHumidity(Float humidity) {
        ApiFetcher.humidity = humidity;
    }

    public void fetchApiData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.18/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HumidityApi humidityApi = retrofit.create(HumidityApi.class);
        Call<String> call = humidityApi.getHumidity();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) { //HTTP Code 200 equals to OK
                    String jsonMessage = response.body();
                    if (StringUtils.isNotBlank(jsonMessage)) {
                        Log.i("HygroSense", "Data fetched: " + jsonMessage);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("HygroSense", "Data call to API failed: " + t);
            }
        });
    }

    public void fetchApiDataInfo() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.18/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SensorDataApi sensorDataApi = retrofit.create(SensorDataApi.class);
        Call<SensorData> call = sensorDataApi.getSensorData();

        call.enqueue(new Callback<SensorData>() {
            @Override
            public void onResponse(Call<SensorData> call, Response<SensorData> response) {
                if (response.code() == 200) { //HTTP Code 200 equals to OK
//                    Float temperature = response.body().getTemperature();
//                    Float humidity = response.body().getHumidity();
//                    temperature = response.body().getTemperature();
//                    humidity = response.body().getHumidity();

                    setTemperature(response.body().getTemperature());
                    setHumidity(response.body().getHumidity());

                    String deviceName = response.body().getDeviceName();
//                    if (StringUtils.isNotBlank(jsonMessage)) {
//                        Log.i("HygroSense", "Data fetched, temp: " + temperature +
//                                " | humidity: " + humidity + " | deviceName: " + deviceName);

                    Log.i("HygroSense", "Data fetched, temp: " + getTemperature() +
                            " | humidity: " + getHumidity() + " | deviceName: " + deviceName);
//                    }
                }
            }

            @Override
            public void onFailure(Call<SensorData> call, Throwable t) {
                Log.i("HygroSense", "Data call to API failed: " + t);
            }
        });
    }
}
