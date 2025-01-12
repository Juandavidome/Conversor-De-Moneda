import Modelos.Moneda;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class PrincipalConversor {
    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://v6.exchangerate-api.com/v6/727b3059028f335232a16071/latest/USD"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(PrincipalConversor::parseResponse)
                .thenAccept(PrincipalConversor::runConverter)
                .join();
    }

    private static Moneda parseResponse(String responseBody) {
        try {
            Gson gson = new Gson();
            // Mapea la estructura JSON a la clase Moneda
            return gson.fromJson(responseBody, Moneda.class);
        } catch (Exception e) {
            // Muestra más información del error para poder depurar
            System.err.println("Error al parsear la respuesta JSON:");
            e.printStackTrace();
            return null;
        }
    }

    private static void runConverter(Moneda moneda) {
        if (moneda == null) {
            System.out.println("Error al obtener las tasas de cambio.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la moneda de origen (ej. USD): ");
        String fromCurrency = scanner.nextLine().toUpperCase();

        System.out.print("Ingrese la moneda de destino (ej. EUR): ");
        String toCurrency = scanner.nextLine().toUpperCase();

        System.out.print("Ingrese la cantidad a convertir: ");
        double amount = scanner.nextDouble();

        Double fromRate = moneda.getConversion_rates().get(fromCurrency);
        Double toRate = moneda.getConversion_rates().get(toCurrency);

        if (fromRate == null || toRate == null) {
            System.out.println("Moneda no soportada.");
        } else {
            double convertedAmount = amount * (toRate / fromRate);
            System.out.printf("%.2f %s son %.2f %s%n", amount, fromCurrency, convertedAmount, toCurrency);
        }
    }
}
