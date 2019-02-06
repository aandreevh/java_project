package hykar.api.service;

import hykar.api.service.exceptions.InvalidServiceConfiguration;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Properties api responsible for
 * loading all application configuration
 * used by other services
 */
public class PropertyService {

    private static final String PROPERTIES_FILE = "properties.ini";
    private static PropertyService instance;

    static {
        instance = new PropertyService();
    }

    private Ini properties;

    private PropertyService() {
        try {
            properties = new Ini(PropertyService.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException e) {
            throw new InvalidServiceConfiguration();
        }
    }

    public static PropertyService getInstance() {
        return instance;
    }

    /**
     * @param section to find the property
     * @param name    of the property
     * @return property value if property exists, empty otherwise
     */
    public Optional<String> getProperty(String section, String name) {

        if (name == null || section == null) throw new NullPointerException();

        String property = properties.get(section, name);

        if (property == null) return Optional.empty();
        return Optional.of(property);
    }

    /**
     * @param section to be searched for properties
     * @return all matching properties from file
     */
    public Map<String, String> getProperties(String section) {
        if (section == null) throw new NullPointerException();

        Map<String, String> propList = new HashMap<>();

        Profile.Section s = properties.get(section);

        if (s == null) return Collections.emptyMap();

        s.forEach(propList::put);

        return propList;
    }


}
