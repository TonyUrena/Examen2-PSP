import redis.clients.jedis.Jedis;

import java.util.Scanner;
import java.util.Set;

public class MeteoClient {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String c = "";
        String[] comando = c.split(" ");

        try (Jedis jedis = new Jedis("184.73.34.167", 6379)) {

            while (!c.equals("EXIT")){

                System.out.print("Introduce un comando:");
                c = sc.nextLine();

                switch (comando[0]) {

                    case "LAST" -> {
                        String ultimaTemp = jedis.hget(("AUR:TEMPERATURES:" + comando[1]), "temperature");

                        System.out.println("Ultima temperatura en " + comando[1] + ": " + ultimaTemp);

                    }
                    case "ALERTS" -> {

                        Set<String> keys = jedis.keys("AUR:ALERTS");
                        if (keys != null) {
                            for (String key : keys) {
                                String alert = jedis.get(key);
                                System.out.println(alert);
                            }
                        }
                    }
                }
            }
        }

    }
}
