package pl.bsotniczuk.hygrosense;

import androidx.appcompat.app.AppCompatActivity;
import pl.bsotniczuk.hygrosense.viewcontroller.MainActivityViewController;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private ApiFetcher apiFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.apiFetcher = new ApiFetcher();

        setContentView(R.layout.activity_main);

        MainActivityViewController mainActivityViewController = new MainActivityViewController(findViewById(R.id.temperatureValueTextView), findViewById(R.id.humidityValueTextView));
    }

    public void getHumidity(View view) {
        this.apiFetcher.fetchApiData();
    }

    public void getDataFromSensor(View view) {
        this.apiFetcher.fetchApiDataInfo();
    }

    public void refreshTextViewThread() {
        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(2000);  //1000ms = 1 sec

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

//                                temperatureValueTextView
//                                textView1.setText(progressData);
//                                textView2.setText(accelerometerDataOutput);
//                                textView3.setText(gyroscopeDataOutput);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

}