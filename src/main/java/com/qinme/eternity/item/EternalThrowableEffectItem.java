package com.qinme.eternity.item;

import com.qinme.eternity.entity.EternalThrownPotion;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;

public class EternalThrowableEffectItem extends Item {

    private static final String[] ROMAN = { "I", "II", "III", "IV", "V" };
    private final Holder<MobEffect> effect;
    private final int amplifier;

    public EternalThrowableEffectItem(Properties properties, Holder<MobEffect> effect) {
        this(properties, effect, 0);
    }

    public EternalThrowableEffectItem(Properties properties, Holder<MobEffect> effect, int amplifier) {
        super(properties);
        this.effect = effect;
        this.amplifier = amplifier;
    }

    public Holder<MobEffect> getEffect() {
        return effect;
    }

    public int getAmplifier() {
        return amplifier;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F,
                0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            EternalThrownPotion thrownpotion = new EternalThrownPotion(level, player, effect, amplifier);
            thrownpotion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
            level.addFreshEntity(thrownpotion);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        String name = Component.translatable(effect.value().getDescriptionId()).getString();
        if (amplifier > 0) {
            name += " " + toRoman(amplifier + 1);
        }
        tooltipComponents.add(Component.literal(name + " (5:00)").withStyle(ChatFormatting.BLUE));
    }

    private static String toRoman(int n) {
        return switch (n) {
            case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; case 4 -> "IV";
            default -> String.valueOf(n);
        };
    }

    private static int rainbowColor() {
        long time = System.currentTimeMillis() / 30L;
        int hue = (int) (time % 360);
        return EternalFoodItem.hslToRgb(hue / 360f, 0.9f, 0.65f);
    }
}
