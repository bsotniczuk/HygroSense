package pl.bsotniczuk.hygrosense.api;

import pl.bsotniczuk.hygrosense.model.ConfigData;
import pl.bsotniczuk.hygrosense.model.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ConfigApi {

    //TODO: to delete
    //http://192.168.4.1
    @POST("config")
    Call<ConfigData> createPost(
            @Body ConfigData configData
    );

    //http://192.168.4.1
    @FormUrlEncoded
    @POST("config")
    Call<ResponseBody> createPost2(
            @Field("wifi_ssid") String wifiSsid,
            @Field("wifi_password") String wifiPassword
    );
}
