package net.blancworks.figura;

import me.shedaniel.architectury.Architectury;
import me.shedaniel.architectury.event.events.client.ClientLifecycleEvent;
import me.shedaniel.architectury.event.events.client.ClientTickEvent;
import net.blancworks.figura.customization.avatar.FiguraAvatar;
import net.blancworks.figura.customization.avatar.FiguraAvatarManager;
import net.blancworks.figura.lua.FiguraLuaManager;
import net.blancworks.figura.network.FiguraNetworkHandler;
import net.blancworks.figura.network.FiguraNetworkManager;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FiguraMod {
    public static final String MOD_ID = "figura";

    /***
     * This is the asynchronous task that Figura uses to do most of it's operations.
     * Stuff like loading avatars off of cache/network is done here.
     */
    private static CompletableFuture mainTask;

    public static void init() {
        mainTask = CompletableFuture.completedFuture(null);

        
        //Run the SSL fixer to trust the backend's certificates.
        try {
            SSLFixer.main();
        } catch (Exception e){
          e.printStackTrace();  
        }
        
        //Setup lua natives
        FiguraLuaManager.setupNativesForLua();

        ClientTickEvent.CLIENT_POST.register((c)->{
            FiguraNetworkManager.handler.tick();
        });
        
        FiguraAvatarManager.init();
    }

    public static FiguraAvatar getOrLoadAvatar(UUID entityUUID) {
        //Attempt to get existing avatar.
        FiguraAvatar currAvatar = FiguraAvatarManager.getAvatar(entityUUID);

        //If there is no avatar for the entity at the given UUID, make one, and then request 
        if (currAvatar == null) {
            currAvatar = FiguraAvatarManager.generateNewAvatar(entityUUID);
        }

        return currAvatar;
    }


    /***
     * Sets up an asynchronous task to be done at some point in the future.
     * @param runnable The runnable to run, eventually.
     * @return The CompletableFuture for the task in question.
     */
    public static synchronized CompletableFuture doTask(Runnable runnable) {
        mainTask = mainTask.thenRunAsync(runnable);
        return mainTask;
    }
}
