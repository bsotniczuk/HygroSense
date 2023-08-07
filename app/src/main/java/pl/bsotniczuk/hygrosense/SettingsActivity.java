package pl.bsotniczuk.hygrosense;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import okhttp3.OkHttpClient;
import pl.bsotniczuk.hygrosense.api.ConfigApi;
import pl.bsotniczuk.hygrosense.controller.DatabaseController;
import pl.bsotniczuk.hygrosense.controller.ToolbarSettingsController;
import pl.bsotniczuk.hygrosense.data.DbConstants;
import pl.bsotniczuk.hygrosense.data.model.SettingsItem;
import pl.bsotniczuk.hygrosense.data.viewmodel.SettingsViewModel;
import pl.bsotniczuk.hygrosense.model.ConfigData;
import pl.bsotniczuk.hygrosense.model.ResponseBody;
import pl.bsotniczuk.hygrosense.viewcontroller.MainActivityViewController;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.app.Activity;
import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity {

    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT
    };

    SettingsViewModel settingsViewModel;
    SettingsItem settingsItem;

    EditText ipAddressEditText;
    EditText wifiSsidEditText;
    EditText wifiPasswordEditText;
    EditText awsEndpointAddressEditText;
    EditText wifiAccessPointSsidEditText;
    EditText wifiPasswordAccessPointEditText;

    SwitchCompat toggleApModeSwitch;

    static BluetoothAdapter bluetoothAdapter;
    static final int REQUEST_BLUETOOTH = 1;

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
                MainActivityViewController.ipAddress = settingsItem.getEsp32_ip_address_access_point();
                Log.i("HygroSense Db Settings", "settings: wifi_ssid: " + settingsItem.getWifi_ssid() + " | esp32ipAddress:" + settingsItem.getEsp32_ip_address_access_point() + " | toString: " + settingsItem.toString());
            }
            else {
                Log.i("HygroSense Db", "I think that the database is non existant");
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

    private void requestBlePermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ActivityCompat.requestPermissions(activity, ANDROID_12_BLE_PERMISSIONS, requestCode);
        else
            btActions();
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.e("Activity result", "OK");
                    // There are no request codes
                    Intent data = result.getData();
                }
            });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                btActions();
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void btActions() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activityResultLauncher.launch(intent);
        } else {
            //Bind the service if BT is enabled
//                getApplicationContext().bindService(new Intent(this, BtleService.class),
//                        this, Context.BIND_AUTO_CREATE);
        }
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

    public void startBtAndConnectToEsp32() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //confirming devices bluetooth connectivity ability
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            requestBlePermissions(this, REQUEST_BLUETOOTH);
        }
    }

    public void sendSettingsToEsp32(View view) {
        ConfigData configData = new ConfigData();
        configData.setWifi_ssid(wifiSsidEditText.getText().toString());
        configData.setWifi_password(wifiPasswordEditText.getText().toString());
        postData(configData);
    }

    private void postData(ConfigData configData) {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.SECONDS)
                .connectTimeout(1, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.4.1/") //TODO: get this from settings
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        ConfigApi configApi = retrofit.create(ConfigApi.class);
        Call<ResponseBody> call = configApi.createPost2(configData.getWifi_ssid(), configData.getWifi_password());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody responseFromAPI = response.body();
                if (response == null) {
                    Log.i("HygroSense", "Response is null");
                }
                    Log.i("HygroSense", "Response code: " + response.code());
                if (responseFromAPI == null) {
                    Log.i("HygroSense", "Response body is null");
                }
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
    public void onBackPressed() {
        super.onBackPressed();
//        startActivity(MainActivity.class);
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        checkIfSettingsChanged(settingsItem);

        this.startActivity(intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        this.finishAfterTransition();
//        this.startActivity(intent);
//        this.finish();
    }
}