package hykar.food.common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Nutrient implements Serializable {

    private String name;
    private float value;
    private String unit;


    public Nutrient(String name, float value, String unit) {
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    /**
     * Parses objects to JsonArray
     *
     * @param objs to parse
     * @return parsed array of objects
     */
    public static JSONArray toJson(Collection<Nutrient> objs) {
        if (objs == null) throw new NullPointerException();

        JSONArray json = new JSONArray();

        objs.forEach(obj -> json.put(toJson(obj)));

        return json;

    }

    /**
     * Parses object to JsonObject
     *
     * @param obj to parse
     * @return parsed object
     */
    public static JSONObject toJson(Nutrient obj) {
        if (obj == null) throw new NullPointerException();

        JSONObject json = new JSONObject();

        json.put("name", obj.getName());
        json.put("value", obj.getValue());
        json.put("unit", obj.getUnit());

        return json;

    }

    /**
     * Generates nutrient instance from json data
     *
     * @param json data
     * @return nutrient instance
     */
    public static Optional<Nutrient> fromJson(JSONObject json) {
        if (json == null) throw new NullPointerException();

        try {

            return Optional.of(new Nutrient(json.getString("name"),
                    json.getFloat("value"),
                    json.getString("unit")));

        } catch (JSONException e) {
            return Optional.empty();
        }

    }

    /**
     * Generates collection of food instances from
     * json array
     *
     * @param json to parse
     * @return collection of parsed food
     */
    public static Collection<Nutrient> fromJsonArray(JSONArray json) {
        if (json == null) throw new NullPointerException();

        List<Nutrient> nutrients = new LinkedList<>();

        json.forEach(h -> fromJson((JSONObject) h).ifPresent(nutrients::add));

        return nutrients;
    }

    @Override
    public String toString() {
        return String.format("name: %s\nvalue: %.2f %s\n", getName(), getValue(), getUnit());
    }

    public String getName() {
        return name;
    }

    public float getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }


}
