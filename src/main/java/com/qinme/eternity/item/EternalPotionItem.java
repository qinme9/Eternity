package com.qinme.eternity.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;

public class EternalPotionItem extends Item {

    private static final int EFFECT_DURATION = 6000;

    public EternalPotionItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            for (MobEffectInstance effect : contents.getAllEffects()) {
                entity.addEffect(new MobEffectInstance(effect.getEffect(), EFFECT_DURATION, effect.getAmplifier()));
            }
        }
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        int color = rainbowColor();
        TextColor textColor = TextColor.fromRgb(color);
        Component prefix = Component.translatable(this.getDescriptionId(stack))
                .withStyle(style -> style.withColor(textColor));
        return contents.potion()
                .<Component>map(p -> {
                    String path = p.getRegisteredName();
                    path = path.substring(path.indexOf(':') + 1);
                    return prefix.copy()
                            .append(Component.translatable(this.getDescriptionId(stack) + ".effect." + path)
                                    .withStyle(style -> style.withColor(textColor)));
                })
                .orElse(prefix);
    }

    private static int rainbowColor() {
        long time = System.currentTimeMillis() / 30L;
        int hue = (int) (time % 360);
        return EternalFoodItem.hslToRgb(hue / 360f, 0.9f, 0.65f);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        for (MobEffectInstance effect : contents.getAllEffects()) {
            String name = Component.translatable(effect.getEffect().value().getDescriptionId()).getString();
            int level = effect.getAmplifier() + 1;
            if (level > 1) {
                name += " " + toRoman(level);
            }
            tooltipComponents.add(Component.literal(name + " (5:00)").withStyle(ChatFormatting.BLUE));
        }
    }

    private static String toRoman(int n) {
        return switch (n) {
            case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; case 4 -> "IV";
            default -> String.valueOf(n);
        };
    }
}
