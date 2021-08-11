package net.blancworks.figura.forge;

import me.shedaniel.architectury.platform.forge.EventBuses;
import net.blancworks.figura.FiguraMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FiguraMod.MOD_ID)
public class FiguraForgeMod {
    public FiguraForgeMod() {
        EventBuses.registerModEventBus(FiguraMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FiguraMod.init();
    }
}
