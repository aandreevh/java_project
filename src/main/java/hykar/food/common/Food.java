package hykar.food.common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Food implements Serializable {

    private static final Pattern UPC_PATTERN = Pattern.compile("\\s*,\\s*UPC:\\s*(\\d*)\\s*$");

    private String name;
    private String ndbno;
    private String upcCode;
    private String manufacturer;

    private Collection<Nutrient> nutrients;

    public Food(String name, String ndbno, String upcCode, String manufacturer, Collection<Nutrient> nutrients) {
        this.name = name;
        this.ndbno = ndbno;
        this.upcCode = upcCode;
        this.manufacturer = manufacturer;
        this.nutrients = nutrients;
    }

    /**
     * Parses objects to JsonArray
     *
     * @param objs to parse
     * @return parsed array of objects
     */
    public static JSONArray toJson(Collection<Food> objs) {
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
    public static JSONObject toJson(Food obj) {
        if (obj == null) throw new NullPointerException();

        JSONObject json = new JSONObject();

        json.put("name", obj.getName() + ", UPC: " + obj.getUpcCode());
        json.put("ndbno", obj.getNdbno());
        json.put("manu", obj.getManufacturer());
        json.put("nutrients", Nutrient.toJson(obj.getNutrients()));

        return json;

    }

    /**
     * Generates food instance from json data
     *
     * @param json data
     * @return food instance
     */
    public static Optional<Food> fromJson(JSONObject json) {
        if (json == null) throw new NullPointerException();

        try {

            String fname = json.getString("name");
            String ndbno = json.getString("ndbno");
            String manufacturer = json.getString("manu");
            String upc = "";

            Matcher upcCode = UPC_PATTERN.matcher(fname);

            if (upcCode.find()) {
                fname = fname.substring(0, upcCode.start());
                upc = upcCode.group(1);
            }

            Collection<Nutrient> nutrients = Nutrient.fromJsonArray(json.getJSONArray("nutrients"));


            return Optional.of(new Food(fname, ndbno, upc, manufacturer, nutrients));

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
    public static Collection<Food> fromJsonArray(JSONArray json) {
        if (json == null) throw new NullPointerException();

        List<Food> foods = new LinkedList<>();

        json.forEach(h -> fromJson((JSONObject) h).ifPresent(foods::add));

        return foods;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder("name: " + getName() + "\n" +
                "ndbno: " + getNdbno() + "\n" +
                "upc: " + getUpcCode() + "\n" +
                "manufacturer: " + getManufacturer() + "\n" +
                "\nnutrient:\n");

        this.getNutrients().forEach(n -> builder.append("\n").append(n.toString()));

        return builder.toString();
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getName() {
        return name;
    }

    public String getNdbno() {
        return ndbno;
    }

    public String getUpcCode() {
        return upcCode;
    }

    public Collection<Nutrient> getNutrients() {
        return nutrients;
    }


}
