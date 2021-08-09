package org.moon.frangura;

import org.moon.frangura.assets.BlockbenchModelAsset;
import org.moon.frangura.lua.LuaScript;

import java.nio.file.Path;

public class CustomPlayerModel {
    public static CustomPlayerModel TEST_INSTANCE;

    public LuaScript script;
    public BlockbenchModelAsset bbmodel;

    public CustomPlayerModel(String modelFolderName) {
        TEST_INSTANCE = this;

        Path p = FranguraMod.getModDirectory().resolve(modelFolderName);
        if (!p.toFile().exists()) {
            FranguraMod.LOGGER.error("Tried to load model \"%s\", but it doesn't exist!", modelFolderName);
            return;
        }
        bbmodel = new BlockbenchModelAsset(p.resolve("model.bbmodel"));
        try {
            script = new LuaScript(modelFolderName + "/script.lua");
        } catch (Exception ignored) {

        }
    }

}
