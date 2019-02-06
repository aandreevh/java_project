package hykar.api.server.command;

import hykar.api.server.command.enums.CommandStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Optional;

public class CommandResponse implements Serializable {

    public static final CommandResponse INVALID_COMMAND_RESPONSE =
            new CommandResponse(CommandStatus.INVALID_COMMAND, "Invalid command.");

    public static final CommandResponse INVALID_ARGUMENTS_RESPONSE =
            new CommandResponse(CommandStatus.INVALID_ARGUMENTS, "Invalid arguments.");

    public static final CommandResponse DISCONNECT_RESPONSE =
            new CommandResponse(CommandStatus.DISCONNECT, "Disconnected from server.");


    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";


    private CommandStatus status;
    private JSONObject response;

    public CommandResponse(CommandStatus status, String message) {
        if (status == null || message == null) throw new NullPointerException();
        this.status = status;
        this.response = new JSONObject() {{
            put(KEY_STATUS, status.getStatus());
            put(KEY_MESSAGE, message);
        }};
    }

    public CommandResponse(CommandStatus status, JSONObject object) {
        if (status == null || object == null) throw new NullPointerException();
        this.status = status;
        this.response = new JSONObject() {{
            put(KEY_STATUS, status.getStatus());
            put(KEY_MESSAGE, object);
        }};
    }

    public CommandResponse(CommandStatus status, JSONArray object) {
        if (status == null || object == null) throw new NullPointerException();
        this.status = status;
        this.response = new JSONObject() {{
            put(KEY_STATUS, status.getStatus());
            put(KEY_MESSAGE, object);
        }};
    }

    /**
     * Creates json object from CommandResponse
     *
     * @param response
     * @return serialized CommandResponse
     */
    public static JSONObject toJson(CommandResponse response) {
        return response.getResponse();
    }

    /**
     * Creates CommandResponse from json object
     *
     * @param json to parse
     * @return parsed CommandResponse
     */
    public static Optional<CommandResponse> fromJson(JSONObject json) {
        if (json == null) throw new NullPointerException();

        try {
            CommandStatus status = CommandStatus.fromStatusCode(json.getInt(KEY_STATUS));
            Object message = json.get(KEY_MESSAGE);

            if (message instanceof String) {
                return Optional.of(new CommandResponse(status, (String) message));
            } else if (message instanceof JSONObject) {
                return Optional.of(new CommandResponse(status, (JSONObject) message));
            } else if (message instanceof JSONArray) {
                return Optional.of(new CommandResponse(status, (JSONArray) message));
            } else {
                return Optional.empty();
            }


        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    public Object getMessage() {
        return (this.getResponse().get(KEY_MESSAGE));
    }

    public CommandStatus getStatus() {
        return status;
    }

    public JSONObject getResponse() {
        return this.response;
    }

    @Override
    public String toString() {
        return getResponse().toString();
    }
}

