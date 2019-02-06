package hykar.api.server.command;

import hykar.api.server.command.interfaces.CommandHandler;

public class DisconnectCommandHandler implements CommandHandler {


    @Override
    public CommandResponse execute(String[] args) {
        return CommandResponse.DISCONNECT_RESPONSE;
    }

    @Override
    public String name() {
        return "disconnect";
    }
}
