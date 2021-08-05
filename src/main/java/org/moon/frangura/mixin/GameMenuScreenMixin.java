package org.moon.frangura.mixin;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.moon.frangura.gui.ModelSelectionScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {

    private ModelSelectionScreen frangura$screen;


    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "initWidgets")
    void initWidgets(CallbackInfo ci) {
        if (frangura$screen == null)
            frangura$screen = new ModelSelectionScreen(this);

        int x = this.width / 2 + 106;
        int y = this.height / 4 + 80;
        // if modmenu button exists
        y -= 12;


        Identifier icon = new Identifier("frangura:textures/gui/logo.png");
        addDrawable(new TexturedButtonWidget(x, y, 20, 20, 0, 0, 20, icon, 20, 40, btn -> {
            this.client.setScreen(frangura$screen);
        }
        ));
    }

}
