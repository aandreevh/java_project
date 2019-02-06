package hykar.food.server.service;

import hykar.api.service.PropertyService;
import hykar.api.service.exceptions.InvalidServiceConfiguration;
import hykar.food.common.Food;
import hykar.food.server.utils.MapUtils;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Service responsible for storing foods already requested
 */
public class FoodCacheService {
    private static final String PROPERTIES_SECTION = "cache-service";
    private static final String PROPERTY_CACHE_FILE = "file";
    private static FoodCacheService instance;

    static {

        instance = new FoodCacheService();

    }

    private String cacheFile;
    private PrintWriter cacheWriter;
    private SortedMap<String, Food> nameFoodMap = new TreeMap<>();
    private Map<String, Food> ndbnoMap = new HashMap<>();
    private Map<String, Food> upcMap = new HashMap<>();
    private ReentrantReadWriteLock lock;

    private FoodCacheService() {

        PropertyService service = PropertyService.getInstance();
        Map<String, String> properties = service.getProperties(PROPERTIES_SECTION);


        if (!properties.containsKey(PROPERTY_CACHE_FILE)) throw new InvalidServiceConfiguration();
        this.cacheFile = properties.get(PROPERTY_CACHE_FILE);

        lock = new ReentrantReadWriteLock();

        try {
            readCacheFile();
            cacheWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getCacheFile(), true)),
                    true);
        } catch (IOException e) {
            throw new InvalidServiceConfiguration();
        }

    }

    public static FoodCacheService getInstance() {
        return instance;
    }

    /**
     * Sync write to cache
     *
     * @param foods to add
     */
    public void addFoodAll(Collection<Food> foods) {
        if (foods == null) throw new NullPointerException();

        if (foods.isEmpty()) return;
        try {
            lock.writeLock().lock();
            for (Food food : foods) {
                if (writeDirtyCache(food)) {
                    writeToCacheFile(food);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Sync write to cache
     *
     * @param food to add
     */
    public void addFood(Food food) {
        if (food == null) return;

        try {
            lock.writeLock().lock();

            if (writeDirtyCache(food)) {
                writeToCacheFile(food);
            }

        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Writes to cache without sync nor record
     *
     * @param food to write
     */
    private boolean writeDirtyCache(Food food) {
        if (!ndbnoMap.containsKey(food.getNdbno())) {
            nameFoodMap.put(food.getName(), food);
            ndbnoMap.put(food.getNdbno(), food);
            if (!food.getUpcCode().isEmpty()) upcMap.put(food.getUpcCode(), food);
            return true;
        }
        return false;
    }

    /**
     * Writes to cache file
     *
     * @param food to write
     */
    private void writeToCacheFile(Food food) {

        cacheWriter.println(Food.toJson(food));

    }

    /**
     * Reads food data from cache file
     */
    private void readCacheFile() {

        try (Scanner reader = new Scanner(new FileReader(this.getCacheFile()))) {

            while (reader.hasNextLine())
                Food.fromJson(new JSONObject(reader.nextLine())).ifPresent(this::writeDirtyCache);


        } catch (IOException e) {
            throw new InvalidServiceConfiguration();
        }

    }

    /**
     * Sync read food data from cache
     *
     * @param name to filter
     * @return food collection found in cache
     */
    public Collection<Food> getFoodByName(String name) {
        if (name == null) throw new NullPointerException();

        Map<String, Food> subMap;
        try {
            lock.readLock().lock();
            subMap = MapUtils.filterPrefix(nameFoodMap, name.toUpperCase() + ",");
        } finally {
            lock.readLock().unlock();
        }

        return subMap.values();
    }

    /**
     * Sync read food data from cache
     *
     * @param ndbno to filter
     * @return food collection found in cache
     */
    public Optional<Food> getFoodByNdbno(String ndbno) {
        if (ndbno == null) throw new NullPointerException();

        Food food;

        try {
            lock.readLock().lock();
            food = ndbnoMap.get(ndbno);
        } finally {
            lock.readLock().unlock();
        }

        return food == null ? Optional.empty() : Optional.of(food);
    }

    /**
     * Sync read food data from cache
     *
     * @param upc to filter
     * @return food collection found in cache
     */
    public Optional<Food> getFoodByUpc(String upc) {
        if (upc == null) throw new NullPointerException();

        Food food;

        try {
            lock.readLock().lock();
            food = upcMap.get(upc);
        } finally {
            lock.readLock().unlock();
        }

        return food == null ? Optional.empty() : Optional.of(food);
    }


    public String getCacheFile() {
        return this.cacheFile;
    }

}
