package net.blancworks.figura.network.handlers;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;
import net.blancworks.figura.network.FiguraNetworkHandler;
import net.blancworks.figura.network.FiguraNetworkManager;
import net.blancworks.figura.network.websockets.FiguraWebsocketHandler;
import net.blancworks.figura.network.websockets.messages.MessageRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.text.Text;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BackendNetworkHandler extends FiguraNetworkHandler {

    public ConnectionState backendConnectionState = ConnectionState.DISCONNECTED;

    private int backendConnectionCooldown = 60;

    protected ClientConnection authConnection;

    /**
     * JWT token obtained from auth. Null if no auth has been performed.
     */
    protected String jwtToken;

    /**
     * The current websocket that the backend connection is using, if any.
     */
    public WebSocket backendWebSocket;

    /**
     * Message registry for the backend.
     */
    public MessageRegistry backendMessageRegistry;
    
    protected final Queue<Runnable> backendTasks = new LinkedList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEntityToAvatarUUID(UUID entityUUID) {
        backendTasks.add(() -> {

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestAvatarFromUUID(UUID avatarUUID) {
        backendTasks.add(() -> {

        });
    }

    @Override
    public void tick() {

        if (authConnection != null)
            authConnection.handleDisconnection();

        if (backendConnectionState == ConnectionState.DISCONNECTED) {
            attemptConnectionToBackend();
        }
    }

    //Minecraft authentication server URL
    public String authServerURL() {
        return "figuranew.blancworks.org";
    }

    //Main server for distributing files URL
    public String mainServerURL() {
        return "https://figuranew.blancworks.org/connect/";
    }

    private void attemptConnectionToBackend() {

        backendConnectionCooldown--;

        if (backendConnectionCooldown <= 0) {
            //Wait 15 seconds between connection attempts.
            backendConnectionCooldown = 20 * 15;

            //Update state
            backendConnectionState = ConnectionState.CONNECTING;

            //Attempt connection.
            backendConnect();
        }
    }

    private void backendConnect() {
        FiguraNetworkManager.doTask(() -> {

            //Attempt auth.
            try {
                Object t = backendAuth().join();
            } catch (Exception e) {
                //AUTH FAILED.
                e.printStackTrace();

                backendConnectionState = ConnectionState.DISCONNECTED;
                return;
            }

            //If no token is available, auth failed.
            if (jwtToken == null) {
                backendConnectionState = ConnectionState.DISCONNECTED;
                return;
            }

            //Attempt to connect to websocket server.
            try {
                //Setup websocket.
                backendWebSocket = new WebSocketFactory().setServerName("figuranew.blancworks.org").createSocket(mainServerURL());
                backendWebSocket.setPingInterval(15 * 1000);
                backendMessageRegistry = new MessageRegistry();

                //Set up websocket message handler.
                FiguraWebsocketHandler websocketHandler = new FiguraWebsocketHandler(this);
                backendWebSocket.addListener(websocketHandler);
                
                //Connect to backend
                backendWebSocket.connect();
                
                //Send auth token
                backendWebSocket.sendText(jwtToken);
                
                //Set up registry.
                FiguraWebsocketHandler.sendClientRegistry(backendWebSocket);
                
                websocketHandler.initializedFuture.join();
            } catch (Exception e) {
                e.printStackTrace();

                backendConnectionState = ConnectionState.DISCONNECTED;
                return;
            }
        });
    }

    public String parseKickAuthMessage(Text reason) {
        if (reason.asString().equals("This is the Figura Auth Server V2.0!\n")) {

            Text tokenText = reason.getSiblings().get(1);

            return jwtToken = tokenText.asString();
            //tokenReceivedTime = new Date();
        }

        return jwtToken = null;
    }

    /**
     * Auths the user using the 'fake' minecraft server that's part of the figura backend.
     */
    private CompletableFuture backendAuth() throws Exception {
        String address = authServerURL();
        InetSocketAddress inetAddress = new InetSocketAddress(address, 25565);

        ClientConnection connection = ClientConnection.connect(inetAddress, true);

        CompletableFuture<String> disconnectedFuture = new CompletableFuture<>();

        //Set listener/handler
        connection.setPacketListener(
                new ClientLoginNetworkHandler(connection, MinecraftClient.getInstance(), null, (text) -> {
                    //FiguraMod.LOGGER.info(text.getString());
                }) {

                    //Handle disconnect message
                    @Override
                    public void onDisconnected(Text reason) {
                        try {
                            Text dcReason = connection.getDisconnectReason();

                            if (dcReason != null) {
                                Text tc = dcReason;
                                disconnectedFuture.complete(parseKickAuthMessage(tc));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //Once connection is closed, yeet this connection so we can make new ones.
                        authConnection = null;

                        disconnectedFuture.complete(null);
                    }
                });

        //Send packets.
        connection.send(new HandshakeC2SPacket(address, 25565, NetworkState.LOGIN));
        connection.send(new LoginHelloC2SPacket(MinecraftClient.getInstance().getSession().getProfile()));

        authConnection = connection;

        return disconnectedFuture;
    }
}
