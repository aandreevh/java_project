package hykar.api.server;

import hykar.api.server.exceptions.ClientConnectionException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Basic handle to a socket
 */
public class SocketHandle implements AutoCloseable {

    private ServerService serverService;
    private Socket socket;

    private PrintWriter writer;
    private Scanner reader;

    private boolean closed = false;

    public SocketHandle(ServerService server, Socket socket) {

        this.serverService = server;
        this.socket = socket;

        try {
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            this.reader = new Scanner(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new ClientConnectionException();
        }

    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        try {
            if (closed) return;
            closed = true;

            if (serverService.unregisterClient(this))
                socket.close();

        } catch (IOException e) {
            throw new ClientConnectionException();
        }
    }

    public PrintWriter getWriter() {
        return this.writer;
    }

    public Scanner getReader() {
        return this.reader;
    }
}
