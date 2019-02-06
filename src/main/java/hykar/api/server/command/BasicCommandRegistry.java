package hykar.api.server.command;

import hykar.api.server.command.interfaces.CommandHandler;
import hykar.api.server.command.interfaces.CommandRegistry;

import java.util.HashMap;
import java.util.Optional;

public class BasicCommandRegistry implements CommandRegistry {
    private HashMap<String, CommandHandler> handlers = new HashMap<>();

    public BasicCommandRegistry() {

        addBasicCommandHandlers();
        addCommandHandlers();

    }

    protected boolean addHandler(CommandHandler handler) {
        if (handler == null) throw new NullPointerException();

        if (handlers.containsKey(handler.name())) return false;

        handlers.put(handler.name(), handler);

        return true;
    }

    private void addBasicCommandHandlers() {
        addHandler(new DisconnectCommandHandler());
    }


    protected void addCommandHandlers() {
    }

    @Override
    public Optional<CommandHandler> getHandler(String name) {
        if (name == null) throw new NullPointerException();

        if (!handlers.containsKey(name)) return Optional.empty();

        return Optional.of(handlers.get(name));
    }
}
