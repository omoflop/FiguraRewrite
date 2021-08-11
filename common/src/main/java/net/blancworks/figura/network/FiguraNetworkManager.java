package net.blancworks.figura.network;

import net.blancworks.figura.network.handlers.CacheNetworkHandler;

import java.util.concurrent.CompletableFuture;

/***
 * The FiguraNetworkManager is the other heart of Figura. It handles all the network processes for Figura
 */
public class FiguraNetworkManager {
    public static FiguraNetworkHandler handler = new CacheNetworkHandler();
    
    private static CompletableFuture networkTasks = CompletableFuture.completedFuture(null);
    
    public static synchronized CompletableFuture doTask(Runnable run){
        networkTasks = networkTasks.thenRunAsync(run);
        return networkTasks;
    }
}
