package hykar.food.client;

import hykar.food.client.FoodClient;
import hykar.food.client.decoder.UPCDecoder;
import hykar.food.common.Food;

import java.util.Collection;
import java.util.Optional;
import java.util.Scanner;

public class StartClient {

    public static void main(String[] arguments) {
        try (FoodClient client = new FoodClient("localhost", 3212)) {

            UPCDecoder decoder = new UPCDecoder();
            Scanner reader = new Scanner(System.in);

            String line = "";
            Collection<Food> foods;
            while (line != null && client.isConnected()) {

                line = reader.nextLine();

                String[] args = line.split("\\s+");

                switch (args[0]) {
                    case "get-food":
                        if (args.length != 2) {
                            System.out.println("Invalid arguments.");
                            continue;
                        }
                        foods = client.requestFoodByName(args[1]);
                        if (foods.isEmpty()) System.out.println("No food data received.");
                        else foods.forEach(System.out::println);
                        break;
                    case "get-food-report":
                        if (args.length != 2) {
                            System.out.println("Invalid arguments.");
                            continue;
                        }
                        foods = client.requestFoodByNdbno(args[1]);
                        if (foods.isEmpty()) System.out.println("No food data received.");
                        else foods.forEach(System.out::println);
                        break;
                    case "get-food-by-barcode":
                        if (args.length != 2) {
                            System.out.println("Invalid arguments.");
                            continue;
                        }
                        if (args[1].startsWith("--upc=")) args[1] = args[1].replace("--upc=", "");
                        else if (args[1].startsWith("--img=")) {
                            args[1] = args[1].replace("--img=", "");
                            Optional<String> upc = decoder.decodeImageUPC(args[1]);
                            if (upc.isEmpty()) {
                                System.out.println("Invalid upc image.");
                                continue;
                            } else args[1] = upc.get();
                        } else {
                            System.out.println("Invalid arguments.");
                            continue;
                        }

                        foods = client.requestFoodByUPC(args[1]);
                        if (foods.isEmpty()) System.out.println("No food data received.");
                        else foods.forEach(System.out::println);
                        break;

                    case "disconnect":

                        if (args.length != 1) {
                            System.out.println("Invalid arguments.");
                            continue;
                        }
                        client.disconnect();
                        client.close();
                        System.out.println("Disconnected.");
                        break;

                    default:
                        System.out.println("Inavlid command.");
                        break;
                }

            }
        } catch (Exception e) {
            System.out.println("Client connection lost.");
        }


    }
}
