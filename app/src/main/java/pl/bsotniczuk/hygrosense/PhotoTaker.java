package pl.bsotniczuk.hygrosense;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;

import pl.bsotniczuk.hygrosense.controller.CameraTextRecognition;


public class PhotoTaker {

    private final MainActivity mainActivity;
    private WindowManager wm;

    public PhotoTaker(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        takePhoto(mainActivity);
    }

    @SuppressWarnings("deprecation")
    private void takePhoto(final Context context) {
        final SurfaceView preview = new SurfaceView(context);
        SurfaceHolder holder = preview.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //required on Android versions prior to 3.0

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            //The preview must happen at or after this point or takePicture fails
            public void surfaceCreated(SurfaceHolder holder) {
                Log.i("HygroSense", "Surface created");
                Camera camera = null;

                try {
                    camera = Camera.open();
                    Log.i("HygroSense", "Opened camera");

                    try {
                        camera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    camera.startPreview();
                    Log.i("HygroSense", "Started preview");

                    Camera.Parameters params = camera.getParameters();
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    camera.setParameters(params);

                    camera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);
                            Log.i("HygroSense", "Took picture");
                            CameraTextRecognition cameraTextRecognition = new CameraTextRecognition(mainActivity);
                            cameraTextRecognition.processImage(picture);
                            camera.release();
                            wm.removeView(preview);
                        }
                    });
                } catch (Exception e) {
                    if (camera != null)
                        camera.release();
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        });

        wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                500, 400, //Must be at least 1x1
                1, 1, //Must be at least 1x1
//                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                0,
                //Don't know if this is a safe default
                PixelFormat.UNKNOWN);

        //Don't set the preview visibility to GONE or INVISIBLE
        wm.addView(preview, params);
    }
}