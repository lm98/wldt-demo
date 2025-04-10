package org.example;

import java.util.Random;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * This class emulates a physical device generating measurements periodically.
 * This emulated device will communicate via MQTT with the WLDT's
 * PhysicalAdapter.
 */
public class MqttNumberProducer {
    private static final String BROKER = System.getenv().getOrDefault("MQTT_BROKER", "127.0.0.1");
    private static final String PORT = System.getenv().getOrDefault("MQTT_PORT", "1883");
    private static final String BROKER_URL = "tcp://" + BROKER + ":" + PORT;
    private MqttClient mqttClient;
    private String sendTopic;

    public MqttNumberProducer(String id, String topic) throws MqttException {
        this.mqttClient = new MqttClient(BROKER_URL, id);
        this.sendTopic = topic;
        var options = new MqttConnectOptions();
        this.mqttClient.connect(options);
    }

    public Runnable emulate() {
        return () -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    var rng = new Random();
                    Double value = rng.nextDouble();
                    var message = new MqttMessage(value.toString().getBytes());
                    System.out.println("sending: " + message.toString() + " on topic " + sendTopic);
                    mqttClient.publish(sendTopic, message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static void main(String[] args) {
        try {
            var id = args[0];
            var topic = args[1];
            new Thread(new MqttNumberProducer(id, topic).emulate()).start();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
