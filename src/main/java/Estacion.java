import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

public class Estacion extends Thread {

    // Simula una estación meteorológica
    // Genera temperaturas aleatorias y las publica en topics MQTT
    private final Random random = new Random();
    private String id = "EST";
    private static int num = 0;
    final float tempMaxima = 40, tempMinima = -10;

    public Estacion() {
        // Usa setName de la clase Thread para darle un ID único a cada Thread
        setName(id += num++);
    }

    public void run() {
        String publisherId = UUID.randomUUID().toString();

        // La IP del servidor es un placeholder, se necesita una dirección real de un servidor MQTT para
        // ejecutar el cliente
        try (MqttClient client = new MqttClient("tcp://184.73.34.167:1883", publisherId)) {

            // Establecemos las opciones para la conexión con MQTT
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(10);
            client.connect(options);

            // Publica un mensaje en un topic con el numero de estacion y la temperatura simulada cada 5 segundos
            while (true) {
                client.publish(
                        "/AUR/METEO/" + id + "/MEASUREMENTS",
                        new MqttMessage((
                                random.nextFloat((tempMaxima - tempMinima) + tempMinima) +
                                        "#" +
                                        LocalDate.now()).getBytes())
                );
                // Esperamos 5 segundos
                sleep(5000);
            }

        } catch (MqttException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
