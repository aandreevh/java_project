package hykar.food.server;

import hykar.api.server.ServerService;

public class StartServer {

    public static void main(String... args) {
        ServerService.getInstance().start();
    }


}
