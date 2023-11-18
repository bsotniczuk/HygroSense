package pl.bsotniczuk.hygrosense.aws;

import android.util.Log;
import android.widget.TextView;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;

import java.util.UUID;

import pl.bsotniczuk.hygrosense.HygroEventListener;
import pl.bsotniczuk.hygrosense.MainActivity;
import pl.bsotniczuk.hygrosense.R;

public class AwsIotCoreIntegration {

    private static final int maxTries = 3;
    private boolean wasConnectionSuccessful;
    private int tries = 0;

    public void setupAwsIotCoreConnection(MainActivity mainActivity, String clientEndpoint, HygroEventListener hygroEventListener) {
        String clientId = UUID.randomUUID().toString();
        String certificateFile = "device_certificate_hygrosense.crt"; // X.509 based certificate file
        String privateKeyFile = "private_key_hygrosense.pem.key"; // PKCS#1 or PKCS#8 PEM encoded private key file

        SampleUtil.KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(mainActivity, certificateFile, privateKeyFile);
        if (pair == null) {
            TextView connectionTextView = mainActivity.findViewById(R.id.connectionTextView);
            connectionTextView.setText(R.string.wrong_aws_certificates);
            return;
        }
        AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);

        String topicName = "hygrosense/pub";
        AWSIotQos qos = AWSIotQos.QOS0;

        HygroSenseTopic topic = new HygroSenseTopic(topicName, qos);
        topic.addListener(hygroEventListener);

        connectToAwsIotMqttClientThread(mainActivity, client, topic);
    }

    private boolean connectToAwsIotMqttClient(MainActivity mainActivity, AWSIotMqttClient client, HygroSenseTopic topic) {
        try {
            client.connect(1500, true);
            Log.i("HygroSense", "Connected to Aws IoT core");
            client.subscribe(topic);
            return true;
        } catch (AWSIotException | AWSIotTimeoutException e) {
            Log.i("HygroSense", "Cannot connect to AwS IoT core, endpoint is unreachable");
            TextView connectionTextView = mainActivity.findViewById(R.id.connectionTextView);
            connectionTextView.setText(R.string.aws_unreachable);
            e.printStackTrace();
            return false;
        }
    }

    public void connectToAwsIotMqttClientThread(MainActivity mainActivity, AWSIotMqttClient client, HygroSenseTopic topic) {
        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted() && tries <= maxTries && !wasConnectionSuccessful) {
                    try {
                        mainActivity.runOnUiThread(() -> {
                            tries++;
                            wasConnectionSuccessful = connectToAwsIotMqttClient(mainActivity, client, topic);
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                interrupt();
            }
        };
        t.start();
    }
}
