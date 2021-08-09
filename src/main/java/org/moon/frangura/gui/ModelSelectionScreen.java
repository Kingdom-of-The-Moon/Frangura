package org.moon.frangura.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.moon.frangura.FranguraMod;
import org.moon.frangura.config.ConfigScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;

public class ModelSelectionScreen extends Screen {
    public Screen parentScreen;

    private static ModelSelectionScreen instance;
    public static ModelSelectionScreen getInstance() {
        return instance;
    }

    public boolean holdingShift = false;
    public boolean holdingControl = false;
    public boolean holdingLeftClick = false;
    public Boolean mouseDragState = null;

    private String[] modelList;
    private int modelSelectorSize = 120;
    private ChatVisibility chatVisibilityBefore;

    // this should probably be used
    private int guiScale = 1;

    private float scroll = 0;
    private float zoomFactor = 1;

    public static final Identifier iconsTexture = new Identifier("frangura:textures/gui/icons.png");

    private TextFieldWidget searchBox;

    private HashMap<TexturedButtonWidget, TranslatableText> buttonsWithTooltips;
    private TexturedButtonWidget reloadButton;
    private TexturedButtonWidget uploadButton;
    private TexturedButtonWidget downloadButton;
    private TexturedButtonWidget deleteButton;
    private TexturedButtonWidget enlargeButton;
    private TexturedButtonWidget helpButton;
    private TexturedButtonWidget settingsButton;
    private TexturedButtonWidget keybindingsButton;
    private TexturedButtonWidget trustButton;
    private static final TranslatableText reloadButtonTooltip      = new TranslatableText("gui.frangura.button.tooltip.reload");
    private static final TranslatableText uploadButtonTooltip      = new TranslatableText("gui.frangura.button.tooltip.upload");
    private static final TranslatableText downloadButtonTooltip    = new TranslatableText("gui.frangura.button.tooltip.download");
    private static final TranslatableText deleteButtonTooltip      = new TranslatableText("gui.frangura.button.tooltip.delete");
    private static final TranslatableText enlargeButtonTooltip     = new TranslatableText("gui.frangura.button.tooltip.enlarge");
    private static final TranslatableText helpButtonTooltip        = new TranslatableText("gui.frangura.button.tooltip.help");
    private static final TranslatableText settingsButtonTooltip    = new TranslatableText("gui.frangura.button.tooltip.settings");
    private static final TranslatableText keybindingsButtonTooltip = new TranslatableText("gui.frangura.button.tooltip.keybindings");
    private static final TranslatableText trustButtonTooltip       = new TranslatableText("gui.frangura.button.tooltip.trust");

    private ArrayList<ModelSelectionWidget> modelSelectionWidgets;

    public ModelSelectionScreen(Screen parentScreen) {
        super(new TranslatableText("gui.frangura.modelselection"));
        this.parentScreen = parentScreen;
        this.instance = this;
    }

    @Override
    public void onClose() {
        this.client.setScreen(parentScreen);
        this.client.options.chatVisibility = this.chatVisibilityBefore;
    }

    @Override
    protected void init() {
        super.init();
        this.chatVisibilityBefore = this.client.options.chatVisibility;
        this.guiScale = (int) this.client.getWindow().getScaleFactor();
        this.buttonsWithTooltips = new HashMap<>();
        this.modelSelectionWidgets = new ArrayList<>();

        this.client.options.chatVisibility = ChatVisibility.HIDDEN;

        // Create a search box, and make it selectable
        this.searchBox = new TextFieldWidget(this.textRenderer, 4, 22, 120, 20, this.searchBox, new TranslatableText("gui.frangura.button.search"));
        this.addSelectableChild(this.searchBox);

        int x = width-20;
        int y = 20;

        this.reloadButton  = makeButton(width/2 + 15, height/2 + 55,20, 32, reloadButtonTooltip, (b) -> {});
        this.uploadButton  = makeButton(width/2 - 10, height/2 + 55,  40, 32, uploadButtonTooltip,      (b) -> {});

        this.enlargeButton = makeButton(width/2 - 35, height/2 + 55, 120, 32, enlargeButtonTooltip,     (b) -> {});

        // temp buttons that don't have a home!!! :<
        this.downloadButton    = makeButton(x, y*3,  60, 32, downloadButtonTooltip,    (b) -> {});
        this.deleteButton      = makeButton(x, y*4, 100, 32, deleteButtonTooltip,      (b) -> {});
        this.helpButton        = makeButton(x, y*6, 140, 32, helpButtonTooltip,        (b) -> {});
        this.settingsButton    = makeButton(x, y*7, 160, 32, settingsButtonTooltip,    (b) -> this.client.setScreen(new ConfigScreen(this)));
        this.keybindingsButton = makeButton(x, y*8, 200, 32, keybindingsButtonTooltip, (b) -> {});
        this.trustButton       = makeButton(x, y*9, 220, 32, trustButtonTooltip,       (b) -> {});

        //        modelEntity = new ClientPlayerEntity(client, (ClientWorld) client.player.world, client.player.networkHandler, client.player.getStatHandler(), client.player.getRecipeBook(), false, false);
        reloadModelList();
    }

    // for now, just gets every file at the mod directory and calls it a "model"
    public void reloadModelList() {
        modelList = FranguraMod.getModDirectory().toFile().list();

        for (int i = 0; i < modelSelectionWidgets.size(); i++) {
            ModelSelectionWidget w = modelSelectionWidgets.get(0);
            remove(w);
        }
        modelSelectionWidgets.clear();

        for (int i = 0; i < modelList.length; i++) {
            int x = 4;
            int y = 50 + (i*modelSelectorSize);

            ModelSelectionWidget w = new ModelSelectionWidget(x,y, modelSelectorSize, modelSelectorSize, MinecraftClient.getInstance().player, modelList[i]);
            modelSelectionWidgets.add(w);
            this.addDrawableChild(w);
        }
    }

    private TexturedButtonWidget makeButton(int x, int y, int u, int v, TranslatableText tooltip, ButtonWidget.PressAction action) {
        TexturedButtonWidget button = new TexturedButtonWidget(x, y, 20, 20, u, v, 20, iconsTexture, 256, 256, action);
        this.buttonsWithTooltips.put(button, tooltip);
        this.addDrawableChild(button);
        return button;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        for (int i = 0; i < modelList.length; i++) {
            ModelSelectionWidget w = modelSelectionWidgets.get(i);

            int size = modelSelectorSize/Math.round(zoomFactor);

            int x = 4 + (i % Math.round(zoomFactor))*size;
            int y = 50 + (int)Math.floor(i / Math.round(zoomFactor))*size;
            //Math.round(MathHelper.lerp(delta/2, w.y, w.originY+scroll))
            w.x = x;
            w.y = y + (int)scroll;
            w.setWidth(size);
            w.setHeight(size);
        }

        // Render search box
        searchBox.render(matrices, mouseX, mouseY, delta);

        // Render model list
        //fillGradient(matrices, 4, 22, modelSelectorSize+4, height-4, 0xFFFFFFFF, 0xFFFFFFFF);
        DrawUtils.drawEntity(width/2, height/2, 1000, 50, 0, 0, MinecraftClient.getInstance().player);

        fillGradient(matrices, width/2 - 50, height/2 - 50, width/2 + 50, height/2 + 50, 0x00000000, 0xAA000000);

        drawTextWithShadow(matrices, this.textRenderer, new LiteralText(Math.floor(zoomFactor)+""),mouseX+8,mouseY,0xFF420696);

        // Draw button tooltips
        for (Map.Entry<TexturedButtonWidget, TranslatableText> buttonTooltip : buttonsWithTooltips.entrySet()) {
            if (buttonTooltip.getKey().isHovered()) {
                if (buttonTooltip.getValue() != null)
                    renderTooltip(matrices, buttonTooltip.getValue(), mouseX, mouseY);
            }
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (holdingControl) {
            zoomFactor += amount;
            zoomFactor = Math.max(1, zoomFactor);
        } else {
            if (mouseX >= 4 && mouseX <= 4+modelSelectorSize && mouseY >= 50 && mouseY <= 50 + (modelList.length*modelSelectorSize)) {
                scroll += amount*16;

                float size = modelSelectorSize/Math.round(zoomFactor);
                int visibleRows = (int)Math.floor(modelList.length / Math.round(zoomFactor))+1;
                int maxRows = (int) ((height+31) / size) - 1;

                float maxScroll = -Math.max(visibleRows, maxRows)*size + (maxRows)*size;
                scroll = Math.min(0, Math.max(scroll,maxScroll));
            }
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL)
            this.holdingControl = true;
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT)
            this.holdingShift = true;

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL)
            this.holdingControl = false;
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT)
            this.holdingShift = false;

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0)
            holdingLeftClick = true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0)
            holdingLeftClick = false;

        mouseDragState = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
