package org.moon.frangura.lua;
import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;
import org.moon.frangura.FranguraMod;
import org.moon.frangura.assets.FranguraAsset;
import org.moon.frangura.lua.api.FileAPI;
import org.terasology.jnlua.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class LuaScript extends FranguraAsset {


    protected LuaState53 state;

    public LuaScript(String scriptName) {
        super(FranguraMod.getModDirectory().resolve(scriptName));
        state = new LuaState53();

        Optional<InputStream> assetStream = getInputStream();
        if (assetStream.isPresent()) {
            try {
                String scriptContent = IOUtils.toString(assetStream.get(), Charsets.UTF_8);

                // Initialize script
                setupGlobals();
                state.load(scriptContent, path.getFileName().toString());
                state.call(0, 0);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.printf("[Frangura] Failed to load script \"%s\"!\n", path.getFileName().toString());
            } catch (LuaException f) {
                f.printStackTrace();
                System.out.println("[Frangura] Script threw an exception on load!");
            }
        } else System.out.printf("[Frangura] Failed to load script \"%s\"!\n", path.getFileName().toString());
    }

    private void setupGlobals() {
        addJavaFunction("log", luaState -> {
            String s = luaState.checkString(1);
            System.out.printf("Fragura >> %s\n", s);
            return 0;
        });
        addAPI("file", new FileAPI());
    }
    private void addAPI(String name, Object object) {
        state.pushJavaObject(object);
        state.setGlobal(name);
    }

    public void addJavaFunction(String name, JavaFunction j) {
        state.pushJavaFunction(j);
        state.setGlobal(name);
    }

    public static void setupLuaNatives() throws ClassNotFoundException {
        File luaLibFile;
        File f;
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        boolean isMacOS = System.getProperty("os.name").toLowerCase().contains("mac");

        // Generate library name.
        StringBuilder builder = new StringBuilder(isWindows ? "libjnlua-" : "jnlua-");

        builder.append("5.3-");

        if (isWindows) {
            // Windows
            builder.append("windows-");
        } else if (isMacOS) {
            builder.append("mac-");
        } else {
            // Assume Linux
            builder.append("linux-");
        }

        if (System.getProperty("os.arch").endsWith("64")) {
            // Assume x86_64
            builder.append("amd64");
        } else {
            // Assume x86_32
            builder.append("i686");
        }

        String ext = "";

        if (isWindows) {
            // Windows
            ext = ".dll";
        } else if (isMacOS) {
            ext = ".dylib";
        } else {
            // Assume Linux
            ext = ".so";
        }


        String targetLib = "/lua_natives/" + builder + ext;

        InputStream libStream = LuaScript.class.getResourceAsStream(targetLib);

        f = new File(builder + ext);

        try {
            System.out.println("stream = " + libStream);
            System.out.println("path = " + f.toPath().toAbsolutePath());
            Files.copy(libStream, f.toPath().toAbsolutePath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }

        NativeSupport.loadPath = f.getAbsolutePath();
    }

}
