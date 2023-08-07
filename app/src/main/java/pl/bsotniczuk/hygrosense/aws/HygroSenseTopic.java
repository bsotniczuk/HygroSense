package pl.bsotniczuk.hygrosense.aws;

import android.util.Log;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import pl.bsotniczuk.hygrosense.HygroEventListener;
import pl.bsotniczuk.hygrosense.model.SensorData;
import pl.bsotniczuk.hygrosense.viewcontroller.MainActivityViewController;

public class HygroSenseTopic extends AWSIotTopic {

    private List<HygroEventListener> listeners = new ArrayList<HygroEventListener>();

    public void addListener(HygroEventListener toAdd) {
        listeners.add(toAdd);
    }

    public HygroSenseTopic(String topic, AWSIotQos qos) {
        super(topic, qos);
    }

    @Override
    public void onMessage(AWSIotMessage message) {
        Log.i("HygroSense", "data received from Aws IoT core :)\n" + message.getStringPayload());

        Gson gson = new Gson();
        SensorData awsSensorData = gson.fromJson(message.getStringPayload(), SensorData.class);

        Log.i("HygroSense", "temperature: " + awsSensorData.getTemperature());
        Log.i("HygroSense", "humidity: " + awsSensorData.getHumidity());

        for (HygroEventListener hl : listeners)
            hl.hygroDataChangedAws(awsSensorData);
    }
}
