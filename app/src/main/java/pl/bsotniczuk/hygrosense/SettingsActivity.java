package pl.bsotniczuk.hygrosense;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import okhttp3.OkHttpClient;
import pl.bsotniczuk.hygrosense.api.ConfigApi;
import pl.bsotniczuk.hygrosense.controller.ToolbarSettingsController;
import pl.bsotniczuk.hygrosense.data.model.SettingsItem;
import pl.bsotniczuk.hygrosense.data.viewmodel.SettingsViewModel;
import pl.bsotniczuk.hygrosense.model.ConfigData;
import pl.bsotniczuk.hygrosense.model.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingsActivity extends AppCompatActivity {

    SettingsViewModel settingsViewModel;
    SettingsItem settingsItem;

    EditText ipAddressEditText;
    EditText wifiSsidEditText;
    EditText wifiPasswordEditText;
    EditText awsEndpointAddressEditText;
    EditText wifiAccessPointSsidEditText;
    EditText wifiPasswordAccessPointEditText;

    SwitchCompat toggleApModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        new ToolbarSettingsController(this, this.findViewById(R.id.toolbar));
        this.ipAddressEditText = this.findViewById(R.id.ipAddressEditText);
        this.wifiSsidEditText = this.findViewById(R.id.wifiSsidEditText);
        this.wifiPasswordEditText = this.findViewById(R.id.wifiPasswordEditText);
        this.awsEndpointAddressEditText = this.findViewById(R.id.awsEndpointAddressEditText);
        this.wifiAccessPointSsidEditText = this.findViewById(R.id.wifiAccessPointSsidEditText);
        this.wifiPasswordAccessPointEditText = this.findViewById(R.id.wifiPasswordAccessPointEditText);
        this.toggleApModeSwitch = this.findViewById(R.id.toggleApModeSwitch);

        setupSettingsObserver();
    }

    private void setupSettingsObserver() {
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        final Observer<SettingsItem> settingsObserver = settingsItem -> {
            if (settingsItem != null) {
                this.settingsItem = settingsItem;
                populateSettingsTextViews(settingsItem);
                Log.i("HygroSense Db Settings", "settings: wifi_ssid: " + settingsItem.getWifi_ssid() + " | esp32ipAddress:" + settingsItem.getEsp32_ip_address_access_point() + " | toString: " + settingsItem.toString());
            }
            else {
                Log.i("HygroSense Db", "Database is non existant");
            }
        };
        settingsViewModel.getSettingsItem().observe(this, settingsObserver);
    }

    private void populateSettingsTextViews(SettingsItem settingsItem) {
        ipAddressEditText.setText(settingsItem.getEsp32_ip_address_access_point());
        wifiSsidEditText.setText(settingsItem.getWifi_ssid());
        wifiPasswordEditText.setText(settingsItem.getWifi_password());
        awsEndpointAddressEditText.setText(settingsItem.getAws_iot_core_endpoint());
        wifiAccessPointSsidEditText.setText(settingsItem.getWifi_access_point_ssid());
        wifiPasswordAccessPointEditText.setText(settingsItem.getWifi_access_point_password());

        if (settingsItem.getConnection_type() == ConnectionType.ACCESS_POINT) {
            toggleApModeSwitch.setChecked(true);
        }
        ipAddressEditText.setEnabled(true);
        wifiSsidEditText.setEnabled(true);
        wifiPasswordEditText.setEnabled(true);
        awsEndpointAddressEditText.setEnabled(true);
        wifiAccessPointSsidEditText.setEnabled(true);
        wifiPasswordAccessPointEditText.setEnabled(true);

        findViewById(R.id.sendSettingsToEsp32).setEnabled(true);
    }

    private void checkIfSettingsChanged(SettingsItem settingsItem) {
        if (checkIfEditTextsChanged()) {
            SettingsItem forUpdate = new SettingsItem(
                    settingsItem.getId(),
                    ipAddressEditText.getText().toString(),
                    wifiSsidEditText.getText().toString(),
                    wifiPasswordEditText.getText().toString(),
                    awsEndpointAddressEditText.getText().toString(),
                    wifiAccessPointSsidEditText.getText().toString(),
                    wifiPasswordAccessPointEditText.getText().toString(),
                    settingsItem.getConnection_type(),
                    settingsItem.getDevice_setting()
            );
            updateSettingsItem(forUpdate);
        }
    }

    private boolean checkIfEditTextsChanged() {
        return !ipAddressEditText.getText().toString().equals(settingsItem.getEsp32_ip_address_access_point())
                || !wifiSsidEditText.getText().toString().equals(settingsItem.getWifi_ssid())
                || !wifiPasswordEditText.getText().toString().equals(settingsItem.getWifi_password())
                || !awsEndpointAddressEditText.getText().toString().equals(settingsItem.getAws_iot_core_endpoint())
                || !wifiAccessPointSsidEditText.getText().toString().equals(settingsItem.getWifi_access_point_ssid())
                || !wifiPasswordAccessPointEditText.getText().toString().equals(settingsItem.getWifi_access_point_password());
    }

    private void updateSettingsItem(SettingsItem settingsItem) {
        settingsViewModel.updateSettingsItem(settingsItem);
    }

    public void switchClicked(View view) {
        int id = view.getId();

        if (id == R.id.toggleApModeSwitch) {
            if (toggleApModeSwitch.isChecked()) {
                switchSetChecked();
            } else if (!toggleApModeSwitch.isChecked()) {
                switchSetUnchecked();
            }
        }
    }

    public void textClicked(View view) {
        int id = view.getId();

        if (id == R.id.textRegular) {
            if (!toggleApModeSwitch.isChecked()) {
                toggleApModeSwitch.setChecked(!toggleApModeSwitch.isChecked());
                switchSetChecked();
            } else if (toggleApModeSwitch.isChecked()) {
                toggleApModeSwitch.setChecked(!toggleApModeSwitch.isChecked());
                switchSetUnchecked();
            }
        }
    }

    public void switchSetChecked() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.access_point_mode), Toast.LENGTH_SHORT).show();
        SettingsItem forUpdate = new SettingsItem(
                settingsItem.getId(),
                settingsItem.getEsp32_ip_address_access_point(),
                settingsItem.getWifi_ssid(),
                settingsItem.getWifi_password(),
                settingsItem.getAws_iot_core_endpoint(),
                settingsItem.getWifi_access_point_ssid(),
                settingsItem.getWifi_access_point_password(),
                ConnectionType.ACCESS_POINT,
                settingsItem.getDevice_setting()
        );
        updateSettingsItem(forUpdate);
    }

    public void switchSetUnchecked() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.aws_mode_station_mode), Toast.LENGTH_SHORT).show();
        SettingsItem forUpdate = new SettingsItem(
                settingsItem.getId(),
                settingsItem.getEsp32_ip_address_access_point(),
                settingsItem.getWifi_ssid(),
                settingsItem.getWifi_password(),
                settingsItem.getAws_iot_core_endpoint(),
                settingsItem.getWifi_access_point_ssid(),
                settingsItem.getWifi_access_point_password(),
                ConnectionType.AWS_IOT_CORE,
                settingsItem.getDevice_setting()
        );
        updateSettingsItem(forUpdate);
    }

    public void sendSettingsToEsp32(View view) {
        ConfigData configData = new ConfigData();
        configData.setWifi_ssid(wifiSsidEditText.getText().toString());
        configData.setWifi_password(wifiPasswordEditText.getText().toString());
        postData(configData, ipAddressEditText.getText().toString());
    }

    private void postData(ConfigData configData, String ipAddress) {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.SECONDS)
                .connectTimeout(1, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ipAddress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        ConfigApi configApi = retrofit.create(ConfigApi.class);
        Call<ResponseBody> call = configApi.createPost(configData.getWifi_ssid(), configData.getWifi_password());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody responseFromAPI = response.body();

                if (responseFromAPI != null) {
                    String responseString = "code: " + response.code() + "\nresponse: " + responseFromAPI.getResponse();
                    Log.i("HygroSense", "Successful POST data call response:\n" + responseString);
                    if (responseFromAPI.getResponse() != null) {
                        Toast.makeText(SettingsActivity.this, responseFromAPI.getResponse(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "Sending data failed\ncheck AP settings", Toast.LENGTH_SHORT).show();
                Log.i("HygroSense", "POST data call to API failed: " + t);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        checkIfSettingsChanged(settingsItem);
    }
}