package net.blancworks.figura;

import net.blancworks.figura.lua.FiguraLuaManager;
import org.terasology.jnlua.LuaState53;

public class FiguraMod {
    public static final String MOD_ID = "figura";
    
    public static void init(){
        //Setup lua natives
        FiguraLuaManager.setupNativesForLua();
    }
}
