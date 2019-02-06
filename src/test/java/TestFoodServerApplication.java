import hykar.api.client.exceptions.ClientDisconnectedException;
import hykar.api.server.ServerService;
import hykar.api.service.PropertyService;
import hykar.food.client.FoodClient;
import hykar.food.client.decoder.UPCDecoder;
import hykar.food.common.Food;
import hykar.food.server.ndb.NDBClient;
import hykar.food.server.service.FoodsService;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.*;

public class TestFoodServerApplication {

    @Before
    public void startServer() {

        (new Thread(() -> {
            ServerService.getInstance().start();
        })).start();

    }

    @Test
    public void TestPropertyService() {
        assertEquals(PropertyService.getInstance().getProperty("default", "version").isPresent(),
                true);
    }


    @Test
    public void TestNDBClient() {

        NDBClient client = new NDBClient("https://api.nal.usda.gov/ndb",
                "wmVx7QYHgmTiZ83ccZajuA0ult9ZEia4IKDpGAKb");

        Collection<Food> foods = client.requestFoodInfo("raffaello");
        assertEquals(foods.size(), 1);

    }

    @Test(expected = ClientDisconnectedException.class)
    public void TestFoodClient() {
        FoodClient client = new FoodClient("localhost", 3212);

        assertEquals(client.requestFoodByName("raffaello").size(), 1);

        assertEquals(client.requestFoodByNdbno("45142036").size(), 1);

        assertEquals(client.requestFoodByUPC("009800146130").size(), 1);

        client.disconnect();
        client.disconnect();
    }

    @Test
    public void TestFoodService() {
        FoodsService service = FoodsService.getInstance();

        Collection<Food> a = service.getFoodDataByName("raffaello");
        Optional<Food> b = service.getFoodDataByNdbno("45142036");
        Optional<Food> c = service.getFoodDataByUpc("009800146130");

        assertFalse(a.isEmpty() || b.isEmpty() || c.isEmpty());

        JSONObject foodA = Food.toJson(a.toArray(new Food[]{})[0]);
        JSONObject foodB = Food.toJson(b.get());
        JSONObject foodC = Food.toJson(c.get());

        assertTrue(foodA.similar(foodB) && foodB.similar(foodC));

    }

    @Test
    public void TestUPCDecoder() {
        UPCDecoder decoder = new UPCDecoder();

        Optional<String> upc1 = decoder.decodeImageUPC("upc/barcode.png");
        Optional<String> upc2 = decoder.decodeImageUPC("upc/raffaello.png");

        assertFalse(upc1.isEmpty() || upc2.isEmpty());
        assertEquals(upc1.get(), "009800146130");
        assertEquals(upc2.get(), "Raffaello");
    }


}
