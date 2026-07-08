package com.qinme.eternity.event;

import com.qinme.eternity.eternity;
import com.qinme.eternity.item.EternalTotemItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = eternity.MODID)
public class EternalEvents {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player) {
            ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);

            ItemStack totem = null;
            InteractionHand hand = null;

            if (mainHand.getItem() instanceof EternalTotemItem) {
                totem = mainHand;
                hand = InteractionHand.MAIN_HAND;
            } else if (offHand.getItem() instanceof EternalTotemItem) {
                totem = offHand;
                hand = InteractionHand.OFF_HAND;
            }

            if (totem != null) {
                event.setCanceled(true);
                player.setHealth(1.0f);
                player.removeAllEffects();
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                player.level().broadcastEntityEvent(player, (byte) 35);
            }
        }
    }
}
