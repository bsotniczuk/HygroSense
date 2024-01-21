package pl.bsotniczuk.hygrosense.calibration;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import pl.bsotniczuk.hygrosense.ConnectionType;
import pl.bsotniczuk.hygrosense.MainActivity;
import pl.bsotniczuk.hygrosense.R;
import pl.bsotniczuk.hygrosense.StaticUtil;
import pl.bsotniczuk.hygrosense.controller.DatabaseController;
import pl.bsotniczuk.hygrosense.model.SensorData;

public class CameraTextRecognition {

    private String[] cameraPermissions;
    private String[] storagePermissions;

    private MainActivity mainActivity;

    private ShapeableImageView imageView;

    private TextRecognizer textRecognizer;
    private TextView textViewMlKit;

    private PopupMenu popupMenu;

    public CameraTextRecognition(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        imageView = mainActivity.findViewById(R.id.imageView);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS); //Or set LATIN
        textViewMlKit = mainActivity.findViewById(R.id.textViewMlKit);

        initInputImageDialog();
    }

    Uri imageUri;

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void pickImageCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Sample Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Description");

        imageUri = mainActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        mainActivity.cameraActivityResultLauncher.launch(intent);
    }

    private void pickImageCameraCalibration() {
        if (!Settings.canDrawOverlays(mainActivity.getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mainActivity.getPackageName()));
            mainActivity.startActivityForResult(intent, StaticUtil.RequestCodes.OVERLAY_REQUEST_CODE);
        } else {
            popupMenu.getMenu().add(Menu.NONE, 5, 5, "Stop calibration");
            new PhotoTaker(mainActivity);
        }
    }

    private void pickImageCameraAutoCalibration() {
        if (!Settings.canDrawOverlays(mainActivity.getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mainActivity.getPackageName()));
            mainActivity.startActivityForResult(intent, StaticUtil.RequestCodes.OVERLAY_REQUEST_CODE);
        } else {
            if (DatabaseController.settingsItem.getConnection_type() == ConnectionType.ACCESS_POINT) {
                StaticUtil.isCalibrationInProgress = true;
                popupMenu.getMenu().add(Menu.NONE, 5, 5, "Stop calibration");
            } else {
                Toast.makeText(mainActivity.getApplicationContext(), "Set device to AP mode", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void pickImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        mainActivity.galleryActivityResultLauncher.launch(intent);
    }

    public void processImage() {
        processImage(imageUri);
    }

    public void processImage(Uri imageUri) {
        this.imageUri = imageUri;
        imageView.setImageURI(imageUri);
        Log.i("HygroSense", "processing image | imageUri: " + imageUri);

        try {
            InputImage inputImage = InputImage.fromFilePath(this.mainActivity, imageUri);
            processImage(inputImage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processImage(Bitmap bitmap) {
        bitmap = rotateBitmap(bitmap, 90);
        imageView.setImageBitmap(bitmap);
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        processImage(inputImage);
    }

    private void processImage(InputImage inputImage) {
        Task<Text> textTaskResult = textRecognizer.process(inputImage).addOnSuccessListener(result -> {
            String resultText = result.getText();
            Log.i("HygroSense", "recognized text:\n" + resultText);

            List<Float> floats = new ArrayList<>();
            for (Text.TextBlock block : result.getTextBlocks()) {
                String blockText = block.getText();

                if (isNumericString(blockText)) {
                    try {
                        blockText = blockText.replaceAll("[^0-9.,]", "");
                        blockText = blockText.replaceAll(",", ".");
                        Float floatValue = Float.parseFloat(blockText);
                        floats.add(floatValue);
                    } catch (NumberFormatException ex) {
                        Log.i("HygroSense", "Wrong value has been passed");
                    }
                }
            }

            SensorData sensorData = new SensorData();
            sensorData.setId(StaticUtil.Constants.referenceSensorId);
            if (floats.size() == 2) {
                floats.get(0); //humidity
                floats.get(1); //temperature

                sensorData.setHumidity(floats.get(0));
                sensorData.setTemperature(floats.get(1));
                mainActivity.hygroDataChangedAutoCalibrationReferenceSensor(sensorData);
            } else {
                Log.i("HygroSense", "Too few or too many float values");
            }

            String log = "";
            for (Float floatValue : floats) {
                log += "floats: " + floatValue + "\n";
                Log.i("HygroSense", "floats:\n" + floatValue);
            }
            log += resultText;

            String finalLog = log;
            mainActivity.runOnUiThread(() -> {
                textViewMlKit.setText(finalLog);
            });
        }).addOnFailureListener(e -> Log.i("HygroSense", "Failed to recognize text"));
    }

    private boolean isNumericString(String blockText) {
        return (blockText.contains(".") || blockText.contains(",")) && !blockText.contains(":");
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this.mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this.mainActivity, storagePermissions, StaticUtil.RequestCodes.STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean cameraResult = ContextCompat.checkSelfPermission(this.mainActivity, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean storageResult = ContextCompat.checkSelfPermission(this.mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return cameraResult && storageResult;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this.mainActivity, cameraPermissions, StaticUtil.RequestCodes.CAMERA_REQUEST_CODE);
    }

    private void requestCameraPermissionCalibration() {
        ActivityCompat.requestPermissions(this.mainActivity, cameraPermissions, StaticUtil.RequestCodes.CAMERA_REQUEST_CODE_CALIBRATION);
    }

    private void requestCameraPermissionAutoCalibration() {
        ActivityCompat.requestPermissions(this.mainActivity, cameraPermissions, StaticUtil.RequestCodes.CAMERA_REQUEST_CODE_AUTO_CALIBRATION);
    }

    public void permissionRelated(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == StaticUtil.RequestCodes.CAMERA_REQUEST_CODE
                || requestCode == StaticUtil.RequestCodes.CAMERA_REQUEST_CODE_CALIBRATION
                || requestCode == StaticUtil.RequestCodes.CAMERA_REQUEST_CODE_AUTO_CALIBRATION) {
            if (grantResults.length > 0) {
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (cameraAccepted) {
                    if (requestCode == StaticUtil.RequestCodes.CAMERA_REQUEST_CODE) {
                        pickImageCamera();
                    } else if (requestCode == StaticUtil.RequestCodes.CAMERA_REQUEST_CODE_CALIBRATION) {
                        pickImageCameraCalibration();
                    } else if (requestCode == StaticUtil.RequestCodes.CAMERA_REQUEST_CODE_AUTO_CALIBRATION) {
                        pickImageCameraAutoCalibration();
                    }
                } else {
                    Toast.makeText(mainActivity.getApplicationContext(), "Camera & Storage permissions are required", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mainActivity.getApplicationContext(), "Cancelled abc1", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == StaticUtil.RequestCodes.STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0) {
//                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

//                if (cameraAccepted) {
                pickImageGallery();
                Toast.makeText(mainActivity.getApplicationContext(), "Unused case I think", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(mainActivity.getApplicationContext(), "Camera & Storage permissions are required", Toast.LENGTH_SHORT).show();
//                }
            }
        }
    }

    private void initInputImageDialog() {
        popupMenu = new PopupMenu(this.mainActivity, mainActivity.findViewById(R.id.toolbar));

        popupMenu.getMenu().add(Menu.NONE, 1, 1, R.string.test_calibration_camera);
        popupMenu.getMenu().add(Menu.NONE, 2, 2, R.string.test_calibration_gallery);
        popupMenu.getMenu().add(Menu.NONE, 3, 3, R.string.calibrate_based_on_reference_sensor_values);
        popupMenu.getMenu().add(Menu.NONE, 4, 4, R.string.auto_calibrate);

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == 1) {
                if (checkCameraPermission()) {
                    pickImageCamera();
                } else {
                    requestCameraPermission();
                }
            } else if (id == 2) {
                if (checkStoragePermission()) {
                    pickImageGallery();
                } else {
                    requestStoragePermission();
                }
            } else if (id == 3) {
                if (checkCameraPermission()) {
                    pickImageCameraCalibration();
                } else {
                    requestCameraPermissionCalibration();
                }
            } else if (id == 4) {
                if (checkCameraPermission()) {
                    pickImageCameraAutoCalibration();
                } else {
                    requestCameraPermissionAutoCalibration();
                }
            } else if (id == 5) {
                StaticUtil.isCalibrationInProgress = false;
                popupMenu.getMenu().removeItem(5);
            }
            return false;
        });
    }

    public void showInputImageDialog() {
        popupMenu.show();
        if (StaticUtil.isCalibrationInProgress && popupMenu.getMenu().size() < 5) {
            popupMenu.getMenu().add(Menu.NONE, 5, 5, R.string.stop_calibration);
        }
    }
}
