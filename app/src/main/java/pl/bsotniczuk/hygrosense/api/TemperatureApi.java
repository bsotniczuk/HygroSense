package pl.bsotniczuk.hygrosense.api;

import retrofit2.Call;
import retrofit2.http.GET;

//TODO: to delete
public interface TemperatureApi {

    @GET("temperature")
    Call<String> getTemperature();
}
