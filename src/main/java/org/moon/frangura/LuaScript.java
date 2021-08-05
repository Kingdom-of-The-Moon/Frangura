package org.moon.frangura;
import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;
import org.terasology.jnlua.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class LuaScript {

    private LuaState53 state;
    private final Path scriptPath;

    public LuaScript(String scriptName) {
        state = new LuaState53();
        scriptPath = FranguraMod.getModDirectory().resolve(scriptName);
        setupGlobals();
        load();
    }

    private void load() {
        try(InputStream inputStream = new FileInputStream(scriptPath.toFile())) {
            String scriptContent = IOUtils.toString(inputStream, Charsets.UTF_8);

            state.load(scriptContent, scriptPath.getFileName().toString());
            state.call(0, 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LuaException e) {
            System.err.printf("LuaError! %s", e);
        }
    }

    private void setupGlobals() {
        addJavaFunction("log", new JavaFunction() {
            @Override
            public int invoke(LuaState luaState) {
                String s = luaState.checkString(1);
                System.out.printf("Fragura >> %s\n", s);
                return 0;
            }
        });
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
