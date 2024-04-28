package backend;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Main {

    public static void main(String[] args) {
        // MQTT connection parameters
        String mqttBroker = "tcp://broker.hivemq.com:1883";
        String mqttClientId = "MQTTSubscriber";
        String mqttTopic = "projekt/gruppe1/sensordaten";

        // Database connection parameters
        String dbUrl = "jdbc:postgresql://127.0.0.1:5432/projekt2";
        String dbUser = "technical";
        String dbPassword = "tech";

        try {
            // MQTT setup
            MqttClient mqttClient = new MqttClient(mqttBroker, mqttClientId, new MemoryPersistence());
            mqttClient.connect();
            System.out.println("Connected to MQTT broker: " + mqttBroker);

            // Database setup
            Connection dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Connected to PostgreSQL database");

            // Subscribe to MQTT topic and process messages
            mqttClient.subscribe(mqttTopic, (topic, message) -> {
                String jsonData = new String(message.getPayload());
                System.out.println("Received JSON data: " + jsonData);

                // Parse JSON data
                JSONObject jsonObject = new JSONObject(jsonData);
                double pressure = jsonObject.getDouble("pressure");
                double height = jsonObject.getDouble("height");
                double temperature = jsonObject.getDouble("temperature");

                // Get current timestamp
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                // Insert data into the database
                try {
                    String sql = "INSERT INTO sensordaten (timestamp, pressure, height, temperature) VALUES (?, ?, ?, ?)";
                    PreparedStatement pstmt = dbConnection.prepareStatement(sql);
                    pstmt.setTimestamp(1, timestamp);
                    pstmt.setDouble(2, pressure);
                    pstmt.setDouble(3, height);
                    pstmt.setDouble(4, temperature);
                    pstmt.executeUpdate();
                    System.out.println("Inserted data into the database");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

        } catch (MqttException | SQLException e) {
            e.printStackTrace();
        }
    }
}