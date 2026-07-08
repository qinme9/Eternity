package com.qinme.eternity.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.BiFunction;

public class EternalThrowableItem extends Item {

    private final BiFunction<Level, Player, ? extends Entity> projectileFactory;
    private final SoundEvent throwSound;

    public EternalThrowableItem(Properties properties,
                                BiFunction<Level, Player, ? extends Entity> projectileFactory,
                                SoundEvent throwSound) {
        super(properties);
        this.projectileFactory = projectileFactory;
        this.throwSound = throwSound;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                throwSound, SoundSource.PLAYERS, 0.5F,
                0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            Entity entity = projectileFactory.apply(level, player);
            level.addFreshEntity(entity);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack))
                .withStyle(style -> style.withColor(TextColor.fromRgb(rainbowColor())));
    }

    private static int rainbowColor() {
        long time = System.currentTimeMillis() / 30L;
        int hue = (int) (time % 360);
        return EternalFoodItem.hslToRgb(hue / 360f, 0.9f, 0.65f);
    }
}
