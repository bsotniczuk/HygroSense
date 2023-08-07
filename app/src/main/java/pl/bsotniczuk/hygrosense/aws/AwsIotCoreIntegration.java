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

    public void setupAwsIotCoreConnection(MainActivity mainActivity, String clientEndpoint, HygroEventListener hygroEventListener) {
        String clientId = UUID.randomUUID().toString();
        String certificateFile = "device_certificate_hygrosense.crt"; // X.509 based certificate file
        String privateKeyFile = "private_key_hygrosense.pem.key"; // PKCS#1 or PKCS#8 PEM encoded private key file

        SampleUtil.KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(mainActivity, certificateFile, privateKeyFile);
        AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);

        String topicName = "hygrosense/pub";
        AWSIotQos qos = AWSIotQos.QOS0;

        HygroSenseTopic topic = new HygroSenseTopic(topicName, qos);
        topic.addListener(hygroEventListener);
        try {
            client.connect(1500, true);
            Log.i("HygroSense", "Connected to Aws IoT core");
            client.subscribe(topic);
        } catch (AWSIotException | AWSIotTimeoutException e) {
            Log.i("HygroSense", "Cannot connect to AwS IoT core, endpoint is unreachable");
            TextView connectionTextView = mainActivity.findViewById(R.id.connectionTextView);
            connectionTextView.setText(R.string.aws_unreachable);
            e.printStackTrace();
        }
    }
}
