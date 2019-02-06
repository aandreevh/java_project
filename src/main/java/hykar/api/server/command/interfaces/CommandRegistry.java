package hykar.api.server.command.interfaces;

import java.util.Optional;

/**
 * Registry that contains all commands
 */
public interface CommandRegistry {

    /**
     * Returns handler by it's name
     *
     * @param name of handler
     * @return handler
     */
    Optional<CommandHandler> getHandler(String name);


}
