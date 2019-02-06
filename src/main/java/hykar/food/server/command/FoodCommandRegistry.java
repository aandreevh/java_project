package hykar.food.server.command;

import hykar.api.server.command.BasicCommandRegistry;

/**
 * Main command registry for the project
 */
public class FoodCommandRegistry extends BasicCommandRegistry {

    @Override
    protected void addCommandHandlers() {
        addHandler(new FoodByNameCommand());
        addHandler(new FoodByNdbnoCommand());
        addHandler(new FoodByUpcCommand());
    }
}
