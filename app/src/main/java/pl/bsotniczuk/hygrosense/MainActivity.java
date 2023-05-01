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

        MainActivityViewController mainActivityViewController =
                new MainActivityViewController(this,
                        this.apiFetcher,
                        findViewById(R.id.temperatureValueTextView),
                        findViewById(R.id.humidityValueTextView)
                );
        this.apiFetcher.addListener(mainActivityViewController);
    }

    public void getHumidity(View view) {
        this.apiFetcher.fetchApiData();
    }

    public void getDataFromSensor(View view) {
        this.apiFetcher.fetchApiDataInfo();
    }

}