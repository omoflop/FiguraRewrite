package net.blancworks.figura.network.websockets;

import com.google.common.io.ByteSource;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;
import com.neovisionaries.ws.client.*;
import net.blancworks.figura.network.handlers.BackendNetworkHandler;
import net.blancworks.figura.network.websockets.messages.MessageHandler;
import net.blancworks.figura.network.websockets.messages.MessageRegistry;
import net.blancworks.figura.network.websockets.messages.avatar.AvatarProvideResponseHandler;
import net.blancworks.figura.network.websockets.messages.avatar.AvatarUploadResponseHandler;
import net.blancworks.figura.network.websockets.messages.pings.PingMessageHandler;
import net.blancworks.figura.network.websockets.messages.pubsub.ChannelAvatarUpdateHandler;
import net.blancworks.figura.network.websockets.messages.user.UserAvatarHashProvideResponseHandler;
import net.blancworks.figura.network.websockets.messages.user.UserAvatarProvideResponseHandler;
import net.blancworks.figura.network.websockets.messages.utility.ErrorMessageHandler;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class FiguraWebsocketHandler extends WebSocketAdapter {

    public static MessageRegistry msgRegistry;

    private static final List<Supplier<MessageHandler>> allMessageHandlers = new ArrayList<Supplier<MessageHandler>>() {{

        add(
                AvatarProvideResponseHandler::new
        );
        add(
                AvatarUploadResponseHandler::new
        );
        add(
                UserAvatarProvideResponseHandler::new
        );
        add(
                UserAvatarHashProvideResponseHandler::new
        );
        add(
                ChannelAvatarUpdateHandler::new
        );
        add(
                PingMessageHandler::new
        );
        add(
                ErrorMessageHandler::new
        );
    }};


    private final BackendNetworkHandler handler;
    public final CompletableFuture<Void> initializedFuture = new CompletableFuture<>();
    
    public FiguraWebsocketHandler(BackendNetworkHandler handler) {
        this.handler = handler;
    }

    public static void sendClientRegistry(WebSocket socket) {
        try {
            try (ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                 LittleEndianDataOutputStream outWriter = new LittleEndianDataOutputStream(outStream)) {
                outWriter.writeInt(allMessageHandlers.size());
                for (Supplier<MessageHandler> handler : allMessageHandlers) {
                    byte[] data = handler.get().getProtocolName().getBytes(StandardCharsets.UTF_8);

                    outWriter.writeInt(data.length);
                    outWriter.write(data);
                }

                socket.sendBinary(outStream.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        super.onConnected(websocket, headers);
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
        super.onConnectError(websocket, exception);
    }

    @Override
    public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
        super.onBinaryMessage(websocket, binary);
        
        try {
            //Get a stream for the bytes
            try (LittleEndianDataInputStream dis = new LittleEndianDataInputStream(ByteSource.wrap(binary).openStream())) {
                if (handler.backendMessageRegistry.isEmpty()) {
                    handler.backendMessageRegistry.readRegistryMessage(dis);

                    //FiguraMod.LOGGER.info("Connection fully initialized.");
                    initializedFuture.complete(null);

                    return;
                }

                //Read the first byte, use that as the ID of the handler.
                int handlerID = dis.readByte() - Byte.MIN_VALUE - 1;

                //If there is a supplier for this ID
                if (allMessageHandlers.size() > handlerID) {
                    //Get the handler.
                    MessageHandler handler = allMessageHandlers.get(handlerID).get();

                    handler.handleMessage(dis);

                } else {
                    //FiguraMod.LOGGER.error("INVALID MESSAGE HANDLER ID " + handlerID);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        super.onCloseFrame(websocket, frame);
    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
    }

    private static WebSocketFactory getSocketFactory() {
        WebSocketFactory factory = new WebSocketFactory();

        return factory;
    }
}
