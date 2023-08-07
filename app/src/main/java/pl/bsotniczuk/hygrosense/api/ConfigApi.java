package pl.bsotniczuk.hygrosense.api;

import pl.bsotniczuk.hygrosense.model.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ConfigApi {

    @FormUrlEncoded
    @POST("config")
    Call<ResponseBody> createPost(
            @Field("wifi_ssid") String wifiSsid,
            @Field("wifi_password") String wifiPassword
    );
}
