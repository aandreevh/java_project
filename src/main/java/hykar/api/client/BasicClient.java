package hykar.api.client;

import hykar.api.client.exceptions.ClientConnectionException;
import hykar.api.client.exceptions.ClientDisconnectedException;
import hykar.api.client.exceptions.ClientResponseException;
import hykar.api.client.interfaces.Client;
import hykar.api.server.command.CommandResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BasicClient implements Client, AutoCloseable {

    private final ExecutorService service = Executors.newSingleThreadExecutor();


    private Socket clientSocket;
    private PrintWriter writer;
    private Scanner reader;

    public BasicClient(String host, int port) {
        connect(host, port);
    }

    @Override
    public CommandResponse request(String... args) {

        try {
            if (clientSocket.isClosed()) throw new ClientDisconnectedException();

            String request = getRequest(args);
            writer.println(request);

            String responseString = reader.nextLine();

            if (responseString == null) throw new ClientDisconnectedException();


            Optional<CommandResponse> response = CommandResponse.fromJson(new JSONObject(responseString));

            if (response.isEmpty()) throw new ClientResponseException();

            return response.get();

        } catch (Exception e) {
            throw new ClientDisconnectedException();
        }
    }

    private void connect(String host, int port) {
        try {

            clientSocket = new Socket(host, port);
            writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            reader = new Scanner(new InputStreamReader(clientSocket.getInputStream()));

        } catch (IOException e) {
            throw new ClientConnectionException();
        }
    }

    /**
     * Creates request from arguments
     *
     * @param args
     * @return string as a request which has to be put
     */
    private String getRequest(String[] args) {
        if (args == null) throw new NullPointerException();
        return String.join(" ", args);
    }

    @Override
    public void close() throws Exception {
        service.shutdown();
        clientSocket.close();
    }

    public boolean isConnected() {

        return !clientSocket.isClosed();
    }
}
