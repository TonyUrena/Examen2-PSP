import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MeteoStations {

    public static void main(String[] args) {

        // Inicializa 10 simulaciones de estaciones meteorologicas
        // cada una en un Thread dentro de un ThreadPool

        final int cantidadEstaciones = 10;
        try (ExecutorService e = Executors.newFixedThreadPool(cantidadEstaciones)) {

            for (int i = 0; i < cantidadEstaciones; i++) {
                e.execute(new Estacion());
            }
        }
    }

}
