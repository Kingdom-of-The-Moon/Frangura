package org.moon.frangura.gui;

import com.mojang.brigadier.StringReader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.moon.frangura.config.Config;

public class ModelSelectionWidget extends PressableWidget {

    public static int modelBoxOutlineBright = 0xFF64364b;
    public static int modelBoxOutlineDark   = 0xFF64364b;
    public static int modelBoxOutlineSelectedBright = 0xFFFFE9E3;
    public static int modelBoxOutlineSelectedDark   = 0xFFF6A2A8;
    public static int modelBoxGradientStart = 0x00000000;
    public static int modelBoxGradientEnd   = 0xAA000000;
    public static int modelBoxTextColor         = 0xFFFFFF;
    public static int modelBoxSelectedTextColor = 0xFFF6A2A8;

    protected boolean selected = false;

    public LivingEntity previewEntity;
    public String modelLabel;

    public float originY;
    private TextRenderer textRenderer;
    private ModelSelectionScreen parent;

    public ModelSelectionWidget(int x, int y, int width, int height, LivingEntity previewEntity, String modelLabel) {
        super(x, y, width, height, new LiteralText(""));
        this.previewEntity = previewEntity;
        this.modelLabel = modelLabel;
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
        this.parent = ModelSelectionScreen.getInstance();
        this.originY = y;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.hovered = isMouseOver(mouseX, mouseY);

        if (parent.holdingLeftClick && parent.holdingShift && this.hovered) {
            if (parent.mouseDragState == null) parent.mouseDragState = selected;
            if (selected != parent.mouseDragState) setSelected(parent.mouseDragState);
        }

        // Gradient background
        fillGradient(matrices, x+1, y+1, x+width-1, y+width-1, modelBoxGradientStart, modelBoxGradientEnd, 0);

        // Outline colors
        boolean s = isHovered() || isFocused() || selected;
        int colA = s ? modelBoxOutlineSelectedBright : modelBoxOutlineBright;
        int colB = s ? modelBoxOutlineSelectedDark : modelBoxOutlineDark;

        // Draw outline
        fillGradient(matrices, x+width-1, y, x+width, y+height, colA, colB, 2);
        fillGradient(matrices, x, y, x+1, y+height, colA, colB, 2);
        fill(matrices, x+1, y, x+width-1, y+1, colA);
        fill(matrices, x+1, y+height-1, x+width-1, y+height, colB);

        if ((Boolean) Config.entries.get("showModelPreview").value) {
            // Draw entity (for now, just draws the player)
            DrawUtils.drawEntity(x + width/2, y + height/2,600.0f, Math.min(width,height)/3f,-30, 22.5f, previewEntity);
        }

        matrices.push();
        matrices.translate(0, 0, 100);
        matrices.scale(1.0F, 1.0F, -1.0F);
        if (width < 40) {
            if (isHovered()) {
                matrices.push();
                matrices.translate(0, 0, -799);
                parent.drawTextWithShadow(matrices, this.textRenderer, new LiteralText(modelLabel), mouseX + 8, mouseY, s ?  modelBoxSelectedTextColor : modelBoxTextColor);
                matrices.pop();
            }
        } else {
            String trimmed = textRenderer.getTextHandler().trimToWidth(modelLabel, this.width, Style.EMPTY);
            drawCenteredTextWithShadow(matrices, this.textRenderer, new LiteralText(trimmed).asOrderedText(), x + width/2, y+height-10, s ?  modelBoxSelectedTextColor : modelBoxTextColor);
        }

        matrices.pop();
    }

    @Override
    public boolean isHovered() {
        return super.isHovered();
    }

    @Override
    public void onPress() {
        setSelected(!selected);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        MinecraftClient.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK, 1, selected ? 2f : 1.6f);
    }
    public boolean getSelected() {
        return selected;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.active && this.visible) {

            if (keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_SPACE && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
                return false;
            } else {
                // stop playing that sound!!!!!!!! >:(
                //this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                this.onPress();
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        // idk
    }

    // ????? how is this not a thing, but setWidth is??
    public void setHeight(int height) {
        this.height = height;
    }
}
