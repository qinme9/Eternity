package com.qinme.eternity.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class EternalTotemItem extends Item {

    public EternalTotemItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack))
                .withStyle(style -> style.withColor(TextColor.fromRgb(rainbowColor())));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        tooltipComponents.add(Component.literal("濒死时自动激活").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("生命恢复 II (0:45)").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("伤害吸收 II (5:00)").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.literal("防火 (5:00)").withStyle(ChatFormatting.BLUE));
    }

    private static int rainbowColor() {
        long time = System.currentTimeMillis() / 30L;
        int hue = (int) (time % 360);
        return EternalFoodItem.hslToRgb(hue / 360f, 0.9f, 0.65f);
    }
}
