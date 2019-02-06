package hykar.food.server.ndb;

import hykar.food.common.Food;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class NDBClient {

    private static final String ENDPOINT_QUERY = "/search";
    private static final String ENDPOINT_REPORT = "/reports";


    private String url;
    private String key;

    private HttpClient client;

    public NDBClient(String url, String key) {
        if (url == null || key == null) throw new NullPointerException();

        this.url = url;
        this.key = key;

        client = HttpClient.newHttpClient();
    }

    /**
     * Request list of all food by name
     *
     * @param name to query
     * @return list of all foods
     */
    public Collection<Food> requestFoodInfo(String name) {
        if (name == null) throw new NullPointerException();
        String uName = name.toUpperCase();

        try {
            HttpResponse response = client.send(createFoodQueryRequest(name), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) return Collections.emptyList();

            JSONArray jsonResponse = new JSONObject((String) response.body())
                    .getJSONObject("list")
                    .getJSONArray("item");

            Collection<FoodHeader> headers = FoodHeader.fromJsonArray(jsonResponse);

            return headers.stream()
                    .filter(h -> matchFoodFullName(h, uName))
                    .map(FoodHeader::getNdbno)
                    .map(this::requestFoodNdBno)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

        } catch (IOException | InterruptedException | JSONException | URISyntaxException e) {
            return Collections.emptyList();
        }
    }

    /**
     * Requests food report
     *
     * @param ndbno to report
     * @return food with all data collected
     */
    public Optional<Food> requestFoodNdBno(String ndbno) {
        if (ndbno == null) throw new NullPointerException();

        try {
            HttpResponse response = client.send(createFoodReportRequest(ndbno), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) return Optional.empty();

            JSONObject jsonResponse = new JSONObject((String) response.body())
                    .getJSONObject("report")
                    .getJSONObject("food");

            return Food.fromJson(jsonResponse);

        } catch (IOException | InterruptedException | JSONException | URISyntaxException e) {
            return Optional.empty();
        }

    }

    /**
     * Predicate for name prefix to match
     *
     * @param header for full name
     * @param name   for prefix to match
     * @return true if name is prefix, false otherwise
     */
    private boolean matchFoodFullName(FoodHeader header, String name) {
        if (header == null || name == null) throw new NullPointerException();

        return header.getName().toLowerCase().startsWith(name.toLowerCase() + ",");
    }


    /**
     * Creates a food query for NDB api
     *
     * @param query for food
     * @return request ready for send
     * @throws URISyntaxException if query is incorrect syntax
     */
    private HttpRequest createFoodQueryRequest(String query) throws URISyntaxException {
        if (query == null) throw new NullPointerException();

        String requestUrl = url + ENDPOINT_QUERY + "?api_key=" + key + "&q=" + query;
        return HttpRequest.newBuilder(new URI(requestUrl)).GET().build();
    }

    /**
     * Creates a food report query
     *
     * @param ndbno for food to report
     * @return food report
     * @throws URISyntaxException if ndbno is incorrect syntax
     */
    private HttpRequest createFoodReportRequest(String ndbno) throws URISyntaxException {
        String requestUrl = url + ENDPOINT_REPORT + "?api_key=" + key + "&ndbno=" + ndbno;
        return HttpRequest.newBuilder(new URI(requestUrl)).GET().build();
    }


}
