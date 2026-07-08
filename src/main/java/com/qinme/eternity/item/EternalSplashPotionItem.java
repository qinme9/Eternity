package com.qinme.eternity.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;

public class EternalSplashPotionItem extends SplashPotionItem {

    public EternalSplashPotionItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F,
                0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            // 1.21.1 uses is(Items.SPLASH_POTION) not instanceof, so we need a vanilla item stack
            ItemStack fakeStack = new ItemStack(Items.SPLASH_POTION);
            fakeStack.set(DataComponents.POTION_CONTENTS,
                    itemstack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY));
            ThrownPotion thrownpotion = new ThrownPotion(level, player);
            thrownpotion.setItem(fakeStack);
            thrownpotion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
            level.addFreshEntity(thrownpotion);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public Component getName(ItemStack stack) {
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        int color = rainbowColor();
        TextColor textColor = TextColor.fromRgb(color);
        Component prefix = Component.translatable(this.getDescriptionId())
                .withStyle(style -> style.withColor(textColor));
        return contents.potion()
                .<Component>map(p -> {
                    String path = p.getRegisteredName();
                    path = path.substring(path.indexOf(':') + 1);
                    return prefix.copy()
                            .append(Component.translatable("item.eternity.eternal_potion.effect." + path)
                                    .withStyle(style -> style.withColor(textColor)));
                })
                .orElse(prefix);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        for (MobEffectInstance effect : contents.getAllEffects()) {
            String name = Component.translatable(effect.getEffect().value().getDescriptionId()).getString();
            if (effect.getAmplifier() > 0) {
                name += " " + toRoman(effect.getAmplifier() + 1);
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

    private static int rainbowColor() {
        long time = System.currentTimeMillis() / 30L;
        int hue = (int) (time % 360);
        return EternalFoodItem.hslToRgb(hue / 360f, 0.9f, 0.65f);
    }
}
