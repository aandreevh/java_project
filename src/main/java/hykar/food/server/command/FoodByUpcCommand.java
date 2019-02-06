package hykar.food.server.command;

import hykar.api.server.command.CommandResponse;
import hykar.api.server.command.enums.CommandStatus;
import hykar.api.server.command.interfaces.CommandHandler;
import hykar.food.common.Food;
import hykar.food.server.service.FoodsService;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Optional;

public class FoodByUpcCommand implements CommandHandler {

    @Override
    public CommandResponse execute(String[] args) {

        if (args.length != 2) return CommandResponse.INVALID_ARGUMENTS_RESPONSE;

        Optional<Food> food = FoodsService.getInstance().getFoodDataByUpc(args[1]);

        if (food.isPresent())
            return new CommandResponse(CommandStatus.SUCCESS, Food.toJson(Collections.singletonList(food.get())));
        else return new CommandResponse(CommandStatus.SUCCESS, new JSONObject());


    }


    @Override
    public String name() {
        return "get-food-by-barcode";
    }
}
