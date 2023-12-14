import redis.clients.jedis.Jedis;

import org.eclipse.paho.client.mqttv3.*;
import java.util.UUID;

public class MeteoServer {

    //  Servidor Redis suscrito a un topic MQTT

    public static void main(String[] args) {

        String publisherId = UUID.randomUUID().toString();

        // Establecemos una conexion con el servidor redis
        try (Jedis jedis = new Jedis("184.73.34.167", 6379)) {

            // Creamos un cliente MQTT y lo conectamos al servidor MQTT
            try (MqttClient client = new MqttClient("tcp://184.73.34.167:1883", publisherId)){

                // Establecemos las opciones para la conexion con MQTT
                MqttConnectOptions options = new MqttConnectOptions();
                options.setAutomaticReconnect(true);
                options.setCleanSession(true);
                options.setConnectionTimeout(10);
                client.connect(options);

                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable throwable) {
                        System.out.println("Conexión perdida... " + throwable.getMessage());
                    }

                    @Override
                    public void messageArrived(String t, MqttMessage m){

                        // Dividimos el mensaje recibido por sus identificadores

                        String[] topic = t.substring(1).split("/");
                        String[] mensaje = new String(m.getPayload()).split("#");

                        System.out.println("Temperatura en estacion " + topic[2] + "\n" +
                                mensaje[0] +
                                " en fecha: " +
                                mensaje[1]);

                        String jHash = "AUR:LASTMEASUREMENT:" + topic[2];
                        String jList = "AUR:TEMPERATURES:" + topic[2];

                        jedis.hset(jHash, "datetime", mensaje[1]);
                        jedis.rpush(jList, "temperature", mensaje[0]);

                        float temperatura = Float.parseFloat(mensaje[0]);

                        if (temperatura > 30f || temperatura < 0f) {
                            jedis.set(
                                    "AUR:ALERTS",
                                    String.format("ALERTA temperaturas extremas el " +
                                            mensaje[1] +
                                            " en la estación: " +
                                            topic[2]));
                        }
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken t) {
                        System.out.println("Entrega Completada");
                    }
                });

                client.subscribe("/AUR/METEO/#", 0);

            } catch (MqttException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
