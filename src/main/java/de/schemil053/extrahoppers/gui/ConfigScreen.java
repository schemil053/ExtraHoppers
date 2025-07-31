package de.schemil053.extrahoppers.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static de.schemil053.extrahoppers.Config.*;

public class ConfigScreen extends Screen {
    private final Screen parent;

    private EditBox hopperSpeedField;
    private EditBox hopperPlusField;
    private EditBox hopperPlusPlusField;
    private Button saveButton;

    private int hopperY, plusY, plusPlusY;

    public ConfigScreen(Screen parent) {
        super(Component.translatable("title.extrahoppers.configure"));
        this.parent = parent;
    }


    @Override
    protected void init() {
        int centerX = width / 2;
        int y = height / 4;

        hopperY = y;
        hopperSpeedField = createInput(centerX, y + 12, HOPPER_SPEED.get());

        y += 40;
        plusY = y;
        hopperPlusField = createInput(centerX, y + 12, HOPPER_PLUS_SPEED.get());

        y += 40;
        plusPlusY = y;
        hopperPlusPlusField = createInput(centerX, y + 12, HOPPER_PLUS_PLUS_SPEED.get());

        y += 50;

        saveButton = Button.builder(Component.translatable("extrahoppers.ui.save"), btn -> {
            HOPPER_SPEED.set(clampInput(hopperSpeedField.getValue(), HOPPER_SPEED.get()));
            HOPPER_PLUS_SPEED.set(clampInput(hopperPlusField.getValue(), HOPPER_PLUS_SPEED.get()));
            HOPPER_PLUS_PLUS_SPEED.set(clampInput(hopperPlusPlusField.getValue(), HOPPER_PLUS_PLUS_SPEED.get()));
            SPEC.save();
            this.minecraft.setScreen(parent);
        }).bounds(centerX - 100, y, 200, 20).build();
        addRenderableWidget(saveButton);

        y += 25;

        addRenderableWidget(Button.builder(Component.translatable("extrahoppers.ui.cancel"), btn -> {
            this.minecraft.setScreen(parent);
        }).bounds(centerX - 100, y, 200, 20).build());
    }

    private EditBox createInput(int centerX, int y, int initialValue) {
        EditBox field = new EditBox(font, centerX - 100, y, 200, 20, Component.literal(""));
        field.setValue(String.valueOf(initialValue));
        field.setResponder(s -> validateInputs());
        addRenderableWidget(field);
        return field;
    }

    private void validateInputs() {
        saveButton.active = isValid(hopperSpeedField.getValue()) && isValid(hopperPlusField.getValue()) && isValid(hopperPlusPlusField.getValue());
    }

    private boolean isValid(String value) {
        try {
            int i = Integer.parseInt(value);
            return i >= 1 && i <= 64;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int clampInput(String value, int fallback) {
        try {
            int i = Integer.parseInt(value);
            return Math.max(1, Math.min(64, i));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);

        graphics.drawCenteredString(font, this.title, width / 2, 15, 0xFFFFFF);

        int centerX = width / 2;

        graphics.drawString(font, Component.translatable("extrahoppers.ui.hopper-speed"), centerX - 100, hopperY, 0xFFFFFF);
        graphics.drawString(font, Component.translatable("extrahoppers.ui.hopper-plus-speed"), centerX - 100, plusY, 0xFFFFFF);
        graphics.drawString(font, Component.translatable("extrahoppers.ui.hopper-plus-plus-speed"), centerX - 100, plusPlusY, 0xFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}
