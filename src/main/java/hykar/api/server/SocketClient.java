package hykar.api.server;

import hykar.api.server.command.CommandResponse;
import hykar.api.server.command.enums.CommandStatus;
import hykar.api.server.command.interfaces.CommandHandler;
import hykar.api.server.command.interfaces.CommandRegistry;

import java.io.PrintWriter;
import java.util.Optional;
import java.util.Scanner;

/**
 * Manages current connection
 */
public class SocketClient implements Runnable {

    private SocketHandle clientHandle;

    private CommandRegistry commandRegistry;

    public SocketClient(SocketHandle handle, CommandRegistry registry) {
        if (handle == null || registry == null) throw new NullPointerException();

        this.commandRegistry = registry;
        this.clientHandle = handle;

    }

    /**
     * Handles and closes client handle
     */
    public void run() {

        PrintWriter writer = this.clientHandle.getWriter();
        Scanner reader = this.clientHandle.getReader();

        while (!this.clientHandle.isClosed()) {
            String input = reader.nextLine();

            String[] args = input.split("\\s+");


            Optional<CommandHandler> handler = commandRegistry.getHandler(args.length == 0 ? "" : args[0]);

            if (handler.isPresent()) {

                CommandResponse response = handler.get().execute(args);

                writer.println(response);

                if (response.getStatus() == CommandStatus.DISCONNECT) {
                    this.clientHandle.close();
                }

            } else {
                writer.println(CommandResponse.INVALID_COMMAND_RESPONSE);
            }

        }

    }
}
