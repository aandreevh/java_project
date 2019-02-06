package hykar.food.client;

import hykar.api.client.BasicClient;
import hykar.api.server.command.CommandResponse;
import hykar.food.common.Food;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class FoodClient implements AutoCloseable {

    private BasicClient client;

    public FoodClient(String host, int port) {
        client = new BasicClient(host, port);
    }


    /**
     * Requests food by name
     *
     * @param name to query
     * @return food data
     */
    public Collection<Food> requestFoodByName(String name) {
        CommandResponse response = client.request("get-food", name);

        return collectFood(response);
    }

    /**
     * Requests food by ndbno
     *
     * @param ndbno to query
     * @return food data
     */
    public Collection<Food> requestFoodByNdbno(String ndbno) {
        CommandResponse response = client.request("get-food-report", ndbno);

        return collectFood(response);
    }

    /**
     * Requests food data by barcode
     *
     * @param upc to query
     * @return food data
     */
    public Collection<Food> requestFoodByUPC(String upc) {
        CommandResponse response = client.request("get-food-by-barcode", upc);

        return collectFood(response);
    }

    /**
     * Disconnects client from food server
     */
    public void disconnect() {
        client.request("disconnect");
    }

    /**
     * Collects food from response
     *
     * @param response to collect food from
     * @return collection of foods
     */
    private Collection<Food> collectFood(CommandResponse response) {

        Object foodJson = response.getMessage();


        if (foodJson instanceof JSONObject) {
            Optional<Food> f = Food.fromJson((JSONObject) foodJson);

            if (f.isPresent()) return Collections.singleton(f.get());

            return Collections.emptyList();
        } else if (foodJson instanceof JSONArray) {
            return Food.fromJsonArray((JSONArray) foodJson);
        }

        return Collections.emptyList();

    }


    @Override
    public void close() throws Exception {
        client.close();
    }

    public boolean isConnected() {

        return this.client.isConnected();
    }
}
