package backend;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.sql.*;

public class Main {

    public static void main(String[] args) {
        // MQTT connection parameters
        String mqttBroker = "tcp://broker.hivemq.com:1883";
        String mqttClientId = "MQTTSubscriber";
        String mqttTopic = "project/gruppe1/sensordaten";

        // Database connection parameters
        String dbUrl = "jdbc:postgresql://your_database_host:5432/your_database_name";
        String dbUser = "your_database_username";
        String dbPassword = "your_database_password";

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

                // Insert JSON data into the database
                try {
                    String sql = "INSERT INTO your_table_name (json_column) VALUES (?)";
                    PreparedStatement pstmt = dbConnection.prepareStatement(sql);
                    pstmt.setString(1, jsonData);
                    pstmt.executeUpdate();
                    System.out.println("Inserted JSON data into the database");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

        } catch (MqttException | SQLException e) {
            e.printStackTrace();
        }
    }
}