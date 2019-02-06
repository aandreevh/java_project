package hykar.food.server.ndb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Food header used to store information from NDB queries
 */
public class FoodHeader implements Serializable {

    private String name;
    private String ndbno;

    public FoodHeader(String name, String ndbno) {
        this.name = name;
        this.ndbno = ndbno;
    }

    /**
     * Creates FoodHeader from JsonObject
     *
     * @param json to parse
     * @return FoodHeader if json is correct
     */
    public static Optional<FoodHeader> fromJson(JSONObject json) {
        if (json == null) throw new NullPointerException();

        try {
            return Optional.of(new FoodHeader(json.getString("name"), json.getString("ndbno")));
        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    /**
     * Creates collection of FoodHeader from JsonArray
     *
     * @param json to parse
     * @return collection of FoodHeader
     */
    public static Collection<FoodHeader> fromJsonArray(JSONArray json) {
        if (json == null) throw new NullPointerException();

        List<FoodHeader> headers = new LinkedList<>();

        json.forEach(h -> fromJson((JSONObject) h).ifPresent(headers::add));

        return headers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNdbno() {
        return ndbno;
    }

}
