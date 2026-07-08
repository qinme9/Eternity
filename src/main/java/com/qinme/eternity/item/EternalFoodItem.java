package com.qinme.eternity.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EternalFoodItem extends Item {

    public EternalFoodItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack copy = stack.copy();
        super.finishUsingItem(copy, level, entity);
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack))
                .withStyle(style -> style.withColor(TextColor.fromRgb(rainbowColor())));
    }

    private static int rainbowColor() {
        long time = System.currentTimeMillis() / 30L;
        int hue = (int) (time % 360);
        return hslToRgb(hue / 360f, 0.9f, 0.65f);
    }

    public static int hslToRgb(float h, float s, float l) {
        float c = (1 - Math.abs(2 * l - 1)) * s;
        float x = c * (1 - Math.abs((h * 6) % 2 - 1));
        float m = l - c / 2;
        float r, g, b;
        if (h < 1f / 6) { r = c; g = x; b = 0; }
        else if (h < 2f / 6) { r = x; g = c; b = 0; }
        else if (h < 3f / 6) { r = 0; g = c; b = x; }
        else if (h < 4f / 6) { r = 0; g = x; b = c; }
        else if (h < 5f / 6) { r = x; g = 0; b = c; }
        else { r = c; g = 0; b = x; }
        return ((int) ((r + m) * 255) << 16) | ((int) ((g + m) * 255) << 8) | (int) ((b + m) * 255);
    }
}
