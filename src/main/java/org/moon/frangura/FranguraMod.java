package org.moon.frangura;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.moon.frangura.config.Config;
import org.moon.frangura.lua.LuaScript;

import java.nio.file.Files;
import java.nio.file.Path;

public class FranguraMod implements ClientModInitializer {

	public static final Logger LOGGER = LogManager.getLogger();


	@Override
	public void onInitializeClient() {
		try {
			LuaScript.setupLuaNatives();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Config.initialize();
	}

	public static Path getModDirectory() {
		Path directory = FabricLoader.getInstance().getGameDir().normalize().resolve("frangura");

		try {
			Files.createDirectories(directory);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return directory;
	}
}
