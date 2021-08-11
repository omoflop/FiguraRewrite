package net.blancworks.figura.network;

import java.util.UUID;

public abstract class FiguraNetworkHandler {
    
    /**
     * Requests the UUID of the entity's avatar, using the entity UUID.
     * @param entityUUID The UUID of the entity to check.
     */
    public abstract void onEntityToAvatarUUID(UUID entityUUID);
    
    /**
     * Requests the NBT for an avatar, by UUID. Assumed to eventually get supplied to the client.
     * @param avatarUUID The UUID of the avatar to request
     */
    public abstract void requestAvatarFromUUID(UUID avatarUUID);

    /**
     * Called 20 times per second, runs general network code.
     */
    public abstract void tick();

    /***
     * Used to indicate the connection status between this client and either the server or the backend.
     */
    public enum ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }
}
