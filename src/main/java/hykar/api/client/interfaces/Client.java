package hykar.api.client.interfaces;

import hykar.api.server.command.CommandResponse;

/**
 * Client interface for command server
 */
public interface Client {

    /**
     * Request command to server
     *
     * @param args for the request
     * @return response from server
     */
    CommandResponse request(String... args);
}
