package hykar.food.server.service;

import hykar.api.service.PropertyService;
import hykar.api.service.exceptions.InvalidServiceConfiguration;
import hykar.food.common.Food;
import hykar.food.server.ndb.NDBClient;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Food api responsible for communicating with USA api
 * and delivering food data to other services
 */
public class FoodsService {

    private static final String PROPERTIES_SECTION = "food-service";
    private static final String PROPERTY_API_KEY = "key";
    private static final String PROPERTY_API_URL = "url";
    private static FoodsService instance;

    /**
     * Thread safe singleton creation
     */
    static {
        instance = new FoodsService();
    }

    private String apiKey;
    private String apiUrl;

    private NDBClient apiClient;
    private FoodCacheService cacheService;

    private FoodsService() {

        PropertyService service = PropertyService.getInstance();
        Map<String, String> properties = service.getProperties(PROPERTIES_SECTION);

        if (!properties.containsKey(PROPERTY_API_KEY)) throw new InvalidServiceConfiguration();
        if (!properties.containsKey(PROPERTY_API_URL)) throw new InvalidServiceConfiguration();

        apiKey = properties.get(PROPERTY_API_KEY);
        apiUrl = properties.get(PROPERTY_API_URL);

        apiClient = new NDBClient(apiUrl, apiKey);
        this.cacheService = FoodCacheService.getInstance();

    }

    public static FoodsService getInstance() {
        return instance;
    }

    /**
     * Gets food data from cache or USA API otherwhise.
     *
     * @param name to filter
     * @return food data
     */
    public Collection<Food> getFoodDataByName(String name) {
        if (name == null) throw new NullPointerException();

        Collection<Food> foods = this.cacheService.getFoodByName(name);

        if (foods.isEmpty()) {
            foods = apiClient.requestFoodInfo(name);
            this.cacheService.addFoodAll(foods);
        }

        return foods;
    }

    /**
     * Gets food data from cache or USA API otherwhise.
     *
     * @param ndbno to filter
     * @return food data
     */
    public Optional<Food> getFoodDataByNdbno(String ndbno) {
        if (ndbno == null) throw new NullPointerException();

        Optional<Food> food = this.cacheService.getFoodByNdbno(ndbno);

        if (food.isEmpty()) {
            food = apiClient.requestFoodNdBno(ndbno);
            food.ifPresent(cacheService::addFood);
        }

        return food;
    }

    /**
     * Gets food data from cache if upc is present
     *
     * @param upc to filter
     * @return food data
     */
    public Optional<Food> getFoodDataByUpc(String upc) {
        if (upc == null) throw new NullPointerException();

        return this.cacheService.getFoodByUpc(upc);
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

}
