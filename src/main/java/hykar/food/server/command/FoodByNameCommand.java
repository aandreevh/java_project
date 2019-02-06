package hykar.food.server.command;

import hykar.api.server.command.CommandResponse;
import hykar.api.server.command.enums.CommandStatus;
import hykar.api.server.command.interfaces.CommandHandler;
import hykar.food.common.Food;
import hykar.food.server.service.FoodsService;

import java.util.Collection;

public class FoodByNameCommand implements CommandHandler {
    @Override
    public CommandResponse execute(String[] args) {


        if (args.length != 2) return CommandResponse.INVALID_ARGUMENTS_RESPONSE;

        Collection<Food> foods = FoodsService.getInstance().getFoodDataByName(args[1]);

        return new CommandResponse(CommandStatus.SUCCESS, Food.toJson(foods));

    }

    @Override
    public String name() {
        return "get-food";
    }
}
