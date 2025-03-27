package org.example;

import java.util.Random;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * This class emulates a physical device generating temperature measurements
 * periodically.
 * This emulated device will communicate via MQTT with the WLDT's
 * PhysicalAdapter.
 */
public class Thermometer {
    private static final String BROKER = "127.0.0.1";
    private static final String PORT = "1883";
    private static final String BROKER_URL = "tcp://" + BROKER + ":" + PORT;
    private static final String ID = "my-thermometer";
    private MqttClient mqttClient;

    public Thermometer() throws MqttException {
        this.mqttClient = new MqttClient(BROKER_URL, ID);
        var options = new MqttConnectOptions();
        this.mqttClient.connect(options);
    }

    public Runnable emulate() {
        return () -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    var rng = new Random();
                    Double temperature = rng.nextDouble();
                    var message = new MqttMessage(temperature.toString().getBytes());
                    System.out.println("sending: " + message.toString());
                    mqttClient.publish("sensor/temperature", message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static void main(String[] args) {
        try {
            new Thread(new Thermometer().emulate()).start();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
