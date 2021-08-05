package org.moon.frangura.mixin;

import net.minecraft.client.gui.screen.TitleScreen;
import org.moon.frangura.lua.LuaScript;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.terasology.jnlua.LuaState53;

@Mixin(TitleScreen.class)
public class ExampleMixin {
	@Inject(at = @At("HEAD"), method = "init()V")
	private void init(CallbackInfo info) {

		LuaState53 luaState = new LuaState53();
		try {
			luaState.load("function funny(a,b) return a + b end", "=simple");

			luaState.call(0, 0); // Evaluates the chunk, defining the function

			// prepare to call func
			luaState.getGlobal("funny");
			luaState.pushInteger(1); // put 1 as the first argument
			luaState.pushInteger(2); // put 2 as the second argument

			// call the add function
			luaState.call(2, 1); // 2 arguments, (1 and 2), and returns 1 thing

			int result = (int) luaState.toInteger(1);
			luaState.pop(1); // "pop" result

			System.out.printf("According to lua, 1 + 2 = %s\n", result);

		} finally {
			luaState.close();
		}

		LuaScript hey = new LuaScript("test.lua");

	}
}
