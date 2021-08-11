package net.blancworks.figura.lua;

import org.terasology.jnlua.NativeSupport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FiguraLuaManager {


    public static void setupNativesForLua() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        boolean isMacOS = System.getProperty("os.name").toLowerCase().contains("mac");
        StringBuilder builder = new StringBuilder(isWindows ? "libjnlua-" : "jnlua-");
        builder.append("5.3-");
        if (isWindows) {
            builder.append("windows-");
        } else if (isMacOS) {
            builder.append("mac-");
        } else {
            builder.append("linux-");
        }

        if (System.getProperty("os.arch").endsWith("64")) {
            builder.append("amd64");
        } else {
            builder.append("i686");
        }

        String ext = "";
        if (isWindows) {
            ext = ".dll";
        } else if (isMacOS) {
            ext = ".dylib";
        } else {
            ext = ".so";
        }

        String targetLib = "/natives/" + builder + ext;
        InputStream libStream = FiguraLuaManager.class.getResourceAsStream(targetLib);
        File f = new File(builder + ext);

        try {
            Files.copy(libStream, f.toPath().toAbsolutePath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
        } catch (IOException var9) {
            var9.printStackTrace();
        }

        NativeSupport.loadLocation = f.getAbsolutePath();
    }
}
