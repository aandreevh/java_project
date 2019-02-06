package hykar.api.server.command.interfaces;

import hykar.api.server.command.CommandResponse;

/**
 * Command handler
 */
public interface CommandHandler {

    CommandResponse execute(String[] args);

    String name();
}
