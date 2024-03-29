package pl.bsotniczuk.hygrosense;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import pl.bsotniczuk.hygrosense.model.SensorData;
import pl.bsotniczuk.hygrosense.viewcontroller.MainActivityViewController;


public class MainActivity extends AppCompatActivity {

    MainActivityViewController mainActivityViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityViewController = new MainActivityViewController(this);
    }

    public void hygroDataChangedAutoCalibrationReferenceSensor(SensorData sensorData) {
        mainActivityViewController.hygroDataChangedAutoCalibrationReferenceSensor(sensorData);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (requestCode == StaticUtil.RequestCodes.OVERLAY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) Log.i("HygroSense", "Overlay enabled");
            else Log.i("HygroSense", "Overlay disabled");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mainActivityViewController.permissionRelated(requestCode, permissions, grantResults);
    }

    public ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        mainActivityViewController.processImage();
                    } else {
                        Toast.makeText(getApplicationContext(), "Cancelled Camera", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    public ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Log.i("HygroSense", "data: " + data);
                        Uri imageUri = data.getData();
                        mainActivityViewController.processImage(imageUri);
                    } else {
                        Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
}
