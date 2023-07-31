package pl.bsotniczuk.hygrosense;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import pl.bsotniczuk.hygrosense.controller.ToolbarSettingsController;
import pl.bsotniczuk.hygrosense.data.model.SettingsItem;
import pl.bsotniczuk.hygrosense.data.viewmodel.SettingsViewModel;
import pl.bsotniczuk.hygrosense.viewcontroller.MainActivityViewController;

import android.app.Activity;
import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

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

        setupSettingsObserver();
    }

    private void setupSettingsObserver() {
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        final Observer<SettingsItem> settingsObserver = settingsItem -> {
            if (settingsItem != null) {
                this.settingsItem = settingsItem;
                populateSettingsTextViews(settingsItem);
                MainActivityViewController.ipAddress = settingsItem.getEsp32_ip_address();
                Log.i("HygroSense Db Settings", "settings: wifi_ssid: " + settingsItem.getWifi_ssid() + " | esp32ipAddress:" + settingsItem.getEsp32_ip_address() + " | toString: " + settingsItem.toString());
            }
            else {
                Log.i("HygroSense Db", "I think that the database is non existant");
            }
        };
        settingsViewModel.getSettingsItem().observe(this, settingsObserver);
    }

    private void populateSettingsTextViews(SettingsItem settingsItem) {
        ipAddressEditText.setText(settingsItem.getEsp32_ip_address());
        wifiSsidEditText.setText(settingsItem.getWifi_ssid());
        wifiPasswordEditText.setText(settingsItem.getWifi_password());

        ipAddressEditText.setEnabled(true);
        wifiSsidEditText.setEnabled(true);
        wifiPasswordEditText.setEnabled(true);
    }

    private void checkIfSettingsChanged(SettingsItem settingsItem) {
        if (checkIfEditTextsChanged()) {
            SettingsItem forUpdate = new SettingsItem(
                    settingsItem.getId(),
                    ipAddressEditText.getText().toString(),
                    wifiSsidEditText.getText().toString(),
                    wifiPasswordEditText.getText().toString(),
                    settingsItem.getDevice_setting()
            );
            updateSettingsItem(forUpdate);
        }
    }

    private boolean checkIfEditTextsChanged() {
        return !ipAddressEditText.getText().toString().equals(settingsItem.getEsp32_ip_address())
                || !wifiSsidEditText.getText().toString().equals(settingsItem.getWifi_ssid())
                || !wifiPasswordEditText.getText().toString().equals(settingsItem.getWifi_password());
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

    public void startBtAndConnectToEsp32() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //confirming devices bluetooth connectivity ability
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            requestBlePermissions(this, REQUEST_BLUETOOTH);
        }
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