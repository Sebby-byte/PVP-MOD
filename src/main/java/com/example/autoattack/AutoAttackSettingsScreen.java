package com.example.autoattack;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Settings screen: toggle buttons for both features plus the rotation speed slider. */
public class AutoAttackSettingsScreen extends Screen {
    private Button clickButton;
    private Button rotateButton;

    public AutoAttackSettingsScreen() {
        super(Component.literal("Auto Attack Settings"));
    }

    @Override
    protected void init() {
        int w = 220;
        int x = this.width / 2 - w / 2;
        int y = this.height / 2 - 52;

        clickButton = this.addRenderableWidget(Button.builder(clickLabel(), b -> {
            AutoAttackClient.toggleClick(this.minecraft);
            refreshLabels();
        }).bounds(x, y, w, 20).build());

        rotateButton = this.addRenderableWidget(Button.builder(rotateLabel(), b -> {
            AutoAttackClient.toggleRotate(this.minecraft);
            refreshLabels();
        }).bounds(x, y + 26, w, 20).build());

        this.addRenderableWidget(new SpeedSlider(x, y + 56, w, 20));

        this.addRenderableWidget(Button.builder(Component.literal("Done"), b -> this.onClose())
                .bounds(x, y + 88, w, 20).build());
    }

    private static Component clickLabel() {
        return Component.literal("Auto Click (mobs + players): " + (AutoAttackClient.isAutoClick() ? "ON" : "OFF"));
    }

    private static Component rotateLabel() {
        return Component.literal("Auto Rotate (within 6 blocks): " + (AutoAttackClient.isAutoRotate() ? "ON" : "OFF"));
    }

    private void refreshLabels() {
        clickButton.setMessage(clickLabel());
        rotateButton.setMessage(rotateLabel());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        Component title = Component.literal("Auto Attack Settings");
        graphics.text(this.font, title, this.width / 2 - this.font.width(title) / 2,
                this.height / 2 - 76, 0xFFFFFFFF, true);
    }

    @Override
    public void onClose() {
        AutoAttackClient.saveConfig(); // remember the slider value
        super.onClose();
    }

    // ---------------------------------------------------------------- slider

    private static double toSlider(float speed) {
        return (speed - AutoAttackClient.MIN_SPEED) / (AutoAttackClient.MAX_SPEED - AutoAttackClient.MIN_SPEED);
    }

    private static float fromSlider(double value) {
        float raw = AutoAttackClient.MIN_SPEED
                + (float) value * (AutoAttackClient.MAX_SPEED - AutoAttackClient.MIN_SPEED);
        return Math.round(raw * 2f) / 2f; // steps of 0.5
    }

    /** Rotation speed in degrees per tick (1 = slow and smooth, 45 = near-instant). */
    private static class SpeedSlider extends AbstractSliderButton {
        SpeedSlider(int x, int y, int width, int height) {
            super(x, y, width, height, Component.empty(), toSlider(AutoAttackClient.getRotateSpeed()));
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal("Rotation speed: " + fromSlider(this.value) + " deg/tick"));
        }

        @Override
        protected void applyValue() {
            AutoAttackClient.setRotateSpeed(fromSlider(this.value));
        }
    }
}
