package org.moon.frangura.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class ModelSelectionScreen extends Screen {
    public Screen parentScreen;

    private TextFieldWidget searchBox;

    public ModelSelectionScreen(Screen parentScreen) {
        super(new TranslatableText("gui.frangura.modelselection"));
        this.parentScreen = parentScreen;
    }

    @Override
    public void onClose() {
        this.client.setScreen(parentScreen);
    }

    @Override
    protected void init() {
        super.init();

        this.searchBox = new TextFieldWidget(this.textRenderer, 4, 22, 100, 20, this.searchBox, new TranslatableText("gui.frangura.button.search"));

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);

        searchBox.render(matrices, mouseX, mouseY, delta);
    }

}
