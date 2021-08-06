package org.moon.frangura.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.moon.frangura.FranguraMod;

public class ModelSelectionScreen extends Screen {
    public Screen parentScreen;

    private TextFieldWidget searchBox;
    private String[] modelList;
    private int hoveredModel = -1;
    private int selectedModel = -1;

    // this should probably be used
    private int guiScale = 1;

    public static final int modelBoxOutlineBright = 0xFFFFFFFF;
    public static final int modelBoxOutlineDark = 0xFFA0A0A0;
    public static final int modelBoxOutlineSelectedBright = 0xFF00FFFF;
    public static final int modelBoxOutlineSelectedDark = 0xFF00A0A0;
    public static final int modelBoxGradientStart = 0xFF00A0A0;
    public static final int modelBoxGradientEnd = 0xFF00A0A0;
    public static final int modelBoxTextColor = 0xFFFFFF;
    public static final int modelBoxSelectedTextColor = 0x00FFFF;

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

        // Create a search box, and make it selectable
        this.searchBox = new TextFieldWidget(this.textRenderer, 4, 22, 100, 20, this.searchBox, new TranslatableText("gui.frangura.button.search"));
        this.addSelectableChild(this.searchBox);

        // for now, just gets every file at the mod directory and calls it a "model"
        modelList = FranguraMod.getModDirectory().toFile().list();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);

        // Render search box
        searchBox.render(matrices, mouseX, mouseY, delta);

        // Render model "list" (test)
        for (int i = 0; i < modelList.length; i++) {

            int startX = searchBox.x;
            int endX = startX + searchBox.getWidth();
            int startY = searchBox.y + searchBox.getHeight() + (searchBox.getWidth() + 3)*i+3;
            int endY = startY + searchBox.getWidth();

            int midX = startX + searchBox.getWidth()/2;

            // Gradient background
            fillGradient(matrices, startX, startY, endX, endY, modelBoxGradientStart, modelBoxGradientEnd, 45);

            // Outline colors
            boolean s = hoveredModel == i || selectedModel == i;
            int colA = s ? modelBoxOutlineSelectedBright : modelBoxOutlineBright;
            int colB = s ? modelBoxOutlineSelectedDark : modelBoxOutlineDark;

            // Draw outline
            fillGradient(matrices, endX, startY-1, endX+1, endY+1, colA, colB, 2);
            fillGradient(matrices, startX-1, startY-1, startX, endY+1, colA, colB, 2);
            fill(matrices, startX, startY-1, endX, startY, colA);
            fill(matrices, startX, endY+1, endX, endY, colB);

            float modelSize = 25f;
            if (hoveredModel == i)
                modelSize = 45;

            // Draw entity (for now, just draws the player)
            drawEntity(midX, startY + searchBox.getWidth()/2, modelSize, -1,-1,-1,-1, -30, 22.5f, MinecraftClient.getInstance().player);

            // Draw the model's name (for now, a model is literally just any file in the models directory)
            drawCenteredTextWithShadow(matrices, this.textRenderer, new LiteralText(modelList[i]).asOrderedText(), midX, startY+searchBox.getWidth()-12, s ?  modelBoxSelectedTextColor : modelBoxTextColor);

        }


        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (hoveredModel != -1) {
            if (selectedModel != hoveredModel) {
                client.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.3f, 1);
            }

            selectedModel = hoveredModel;
        }


        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);

        hoveredModel = -1;
        for (int i = 0; i < modelList.length; i++) {
            int startX = searchBox.x;
            int endX = startX + searchBox.getWidth();
            int startY = searchBox.y + searchBox.getHeight() + (searchBox.getWidth() + 3) * i + 3;
            int endY = startY + searchBox.getWidth();
            if (mouseX > startX && mouseX < endX && mouseY >= startY && mouseY <= endY) {
                hoveredModel = i;
                break;
            }
        }
    }

    public static void drawEntity(int x, int y, float size, int scissor_x, int scissor_y, int scissor_width, int scissor_height, float rotX, float rotY, LivingEntity entity) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 1500.0D);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0D, 0.0D, 1000.0D);
        matrixStack2.scale(size, size, size);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(rotX);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack2.multiply(quaternion);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        boolean invisible = entity.isInvisible();
        entity.bodyYaw = 180.0F - rotY;
        entity.setYaw(180.0F - rotY);
        entity.setPitch(0.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        entity.setInvisible(false);
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0D, -1.0D, 0.0D, 0.0F, 1.0F, matrixStack2, immediate, 15728880));
        RenderSystem.disableScissor();
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        entity.bodyYaw = h;
        entity.setYaw(i);
        entity.setPitch(j);
        entity.prevHeadYaw = k;
        entity.headYaw = l;
        entity.setInvisible(invisible);
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }

}
