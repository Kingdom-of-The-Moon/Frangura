package org.moon.frangura;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class FranguraMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		try {
			LuaScript.setupLuaNatives();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
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
