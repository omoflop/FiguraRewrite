package net.blancworks.figura.fabric;

import net.blancworks.figura.FiguraMod;
import net.fabricmc.api.ClientModInitializer;

public class FiguraFabricMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FiguraMod.init();
    }
}
