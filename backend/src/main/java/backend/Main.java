package backend;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Main {

    public static void main(String[] args) {
        String broker = "tcp://broker.hivemq.com";
        String clientId = "MQTTSubscriber";
        String topic = "projekt/gruppe1/sensordaten";

        try {
            MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            System.out.println("Connecting to broker: " + broker);
            client.connect(connOpts);
            System.out.println("Connected");

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("Message received:\n" + "  Topic: " + topic + "\n  Message: " + new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // wird nicht im subscriber genutzt, muss aber implementiert werden weil MqttCallback ein Interface ist.
                }
            });

            System.out.println("Subscribing to topic: " + topic);
            client.subscribe(topic);

            // Wait indefinitely to receive messages
            while (true) {
                Thread.sleep(1000); // Adjust as needed
            }

        } catch (MqttException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}