package net.blancworks.figura;

import dev.architectury.event.events.client.ClientTickEvent;
import net.blancworks.figura.customization.avatar.FiguraAvatar;
import net.blancworks.figura.customization.avatar.FiguraAvatarManager;
import net.blancworks.figura.lua.FiguraLuaManager;
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

        ClientTickEvent.CLIENT_POST.register((c)-> FiguraNetworkManager.handler.tick());

        /*
        //EFFECT ATLAS
        CardElement.EFFECT_ATLAS_TEXTURE = new SpriteAtlasTexture(new Identifier("figura", "textures/atlas/card_effects.png"));
        MinecraftClient.getInstance().getTextureManager().registerTexture(CardElement.EFFECT_ATLAS_TEXTURE.getId(), CardElement.EFFECT_ATLAS_TEXTURE);

        Map<Identifier, List<Identifier>> map = Maps.newConcurrentMap();
        List<Identifier> list = List.of(CardElement.CardEffects.LINES.texture);
        map.put(CardElement.CardEffects.LINES.id, list.stream().map((textIdentifier) -> new Identifier(textIdentifier.getNamespace(), "cards/effects/" + textIdentifier.getPath())).collect(Collectors.toList()));

        SpriteAtlasTexture.Data data = CardElement.EFFECT_ATLAS_TEXTURE.stitch(MinecraftClient.getInstance().getResourceManager(), map.values().stream().flatMap(Collection::stream), MinecraftClient.getInstance().getProfiler(), 0);
        CardElement.EFFECT_ATLAS_TEXTURE.upload(data);
         */

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
