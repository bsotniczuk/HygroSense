package pl.bsotniczuk.hygrosense.api;

import pl.bsotniczuk.hygrosense.model.SensorData;
import retrofit2.Call;
import retrofit2.http.GET;

public interface SensorDataApi {

    @GET("data")
    Call<SensorData[]> getSensorData();
}
