package com.qinme.eternity.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class EternalEffectFoodItem extends Item {

    private static final int EFFECT_DURATION = 6000;
    private static final String[] ROMAN = { "I", "II", "III", "IV", "V" };
    private final Holder<MobEffect> effect;
    private final int amplifier;

    public EternalEffectFoodItem(Properties properties, Holder<MobEffect> effect) {
        this(properties, effect, 0);
    }

    public EternalEffectFoodItem(Properties properties, Holder<MobEffect> effect, int amplifier) {
        super(properties);
        this.effect = effect;
        this.amplifier = amplifier;
    }

    public Holder<MobEffect> getEffect() {
        return effect;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            entity.addEffect(new MobEffectInstance(effect, EFFECT_DURATION, amplifier));
        }
        ItemStack copy = stack.copy();
        super.finishUsingItem(copy, level, entity);
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        int color = rainbowColor();
        Component name = Component.translatable(this.getDescriptionId(stack))
                .withStyle(style -> style.withColor(TextColor.fromRgb(color)))
                .copy()
                .append(Component.translatable(effect.value().getDescriptionId())
                        .withStyle(style -> style.withColor(TextColor.fromRgb(color))));
        if (amplifier > 0 && amplifier < ROMAN.length) {
            name = name.copy().append(Component.literal(" " + ROMAN[amplifier])
                    .withStyle(style -> style.withColor(TextColor.fromRgb(color))));
        }
        return name;
    }

    private static int rainbowColor() {
        long time = System.currentTimeMillis() / 30L;
        int hue = (int) (time % 360);
        return EternalFoodItem.hslToRgb(hue / 360f, 0.9f, 0.65f);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        String name = Component.translatable(effect.value().getDescriptionId()).getString();
        int level = amplifier + 1;
        if (level > 1) {
            name += " " + toRoman(level);
        }
        tooltipComponents.add(Component.literal(name + " (5:00)").withStyle(ChatFormatting.BLUE));
    }

    private static String toRoman(int n) {
        return switch (n) {
            case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; case 4 -> "IV";
            default -> String.valueOf(n);
        };
    }
}
