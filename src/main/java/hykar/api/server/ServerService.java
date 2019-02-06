package hykar.api.server;


import hykar.api.server.command.BasicCommandRegistry;
import hykar.api.server.command.interfaces.CommandRegistry;
import hykar.api.server.exceptions.ClientConnectionException;
import hykar.api.server.exceptions.InvalidPortException;
import hykar.api.server.exceptions.ServerDisconnectionException;
import hykar.api.service.PropertyService;
import hykar.api.service.exceptions.InvalidServiceConfiguration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server api responsible for accepting and managing clients
 */
public class ServerService {

    private static final String PROPERTIES_SECTION = "server-service";
    private static final String PROPERTY_PORT = "port";
    private static final String PROPERTY_CONNECTIONS = "connections";
    private static final String PROPERTY_REGISTRY_CLASS = "registry";


    private static ServerService instance = null;

    static {
        instance = new ServerService();
    }

    private Set<SocketHandle> socketHandles = new HashSet<>();
    private PropertyService propertyService;
    private ServerSocket serverSocket;
    private ExecutorService clientsService;
    private CommandRegistry commandRegistry;
    private int port;
    private int connections;
    private boolean started = false;

    private ServerService() {
        propertyService = PropertyService.getInstance();

        Map<String, String> properties = propertyService.getProperties(PROPERTIES_SECTION);

        if (!properties.containsKey(PROPERTY_PORT)) throw new InvalidServiceConfiguration();
        if (!properties.containsKey(PROPERTY_CONNECTIONS)) throw new InvalidServiceConfiguration();


        try {

            this.port = Integer.valueOf(properties.get(PROPERTY_PORT));
            this.connections = Integer.valueOf(properties.get(PROPERTY_CONNECTIONS));
            if (properties.containsKey(PROPERTY_REGISTRY_CLASS)) {
                loadRegistryClass(properties.get(PROPERTY_REGISTRY_CLASS));
            } else loadDefaultRegistryClass();
        } catch (Exception e) {
            throw new InvalidServiceConfiguration();
        }

    }

    public static ServerService getInstance() {
        return instance;
    }

    private void loadDefaultRegistryClass() {

        this.commandRegistry = new BasicCommandRegistry();
    }

    private void loadRegistryClass(String className) throws Exception {
        this.commandRegistry = (CommandRegistry) (Class.forName(className).getDeclaredConstructor().newInstance());
    }

    /**
     * Starts the main server
     */
    public void start() {

        synchronized (this) {
            if (started) return;
            started = true;
        }

        this.clientsService = Executors.newFixedThreadPool(getMaxConnections());

        try {

            this.serverSocket = new ServerSocket(getPort());

        } catch (IOException e) {
            throw new InvalidPortException();
        }

        listenServer();


    }

    /**
     * Starts listening to connections
     */
    private void listenServer() {

        while (true) {

            try {

                Socket clientSocket = this.serverSocket.accept();

                if (!registerClient(clientSocket)) {
                    clientSocket.close();
                }

            } catch (IOException e) {
                try {
                    this.serverSocket.close();
                } catch (IOException ee) {
                    throw new ServerDisconnectionException();
                }

                throw new ClientConnectionException();
            }

        }

    }

    /**
     * Registers client and returns handle to it
     *
     * @return if client registered
     */
    private synchronized boolean registerClient(Socket client) {
        if (client == null) throw new NullPointerException();

        if (isFull()) return false;

        SocketHandle handle = new SocketHandle(this, client);
        socketHandles.add(handle);
        clientsService.submit(new SocketClient(handle, commandRegistry));


        return true;
    }

    /**
     * Unregisters client
     *
     * @param client to unregister
     * @return if client unregistered
     */
    synchronized boolean unregisterClient(SocketHandle client) {
        if (client == null) throw new NullPointerException();

        if (socketHandles.contains(client)) {
            socketHandles.remove(client);
            return true;
        }

        return false;
    }


    public boolean isFull() {
        return socketHandles.size() >= this.getMaxConnections();
    }

    public boolean isStarted() {
        return started;
    }


    public int getMaxConnections() {
        return connections;
    }

    public int getPort() {
        return this.port;
    }


}
