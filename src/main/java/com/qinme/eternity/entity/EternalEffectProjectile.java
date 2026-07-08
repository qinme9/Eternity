package com.qinme.eternity.entity;

import com.qinme.eternity.item.EternalThrowableEffectItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class EternalEffectProjectile extends ThrowableItemProjectile {

    private static final int EFFECT_DURATION = 6000;

    public EternalEffectProjectile(EntityType<? extends EternalEffectProjectile> type, Level level) {
        super(type, level);
    }

    public EternalEffectProjectile(Level level, LivingEntity shooter) {
        super((EntityType<? extends EternalEffectProjectile>) BuiltInRegistries.ENTITY_TYPE
                .get(ResourceLocation.fromNamespaceAndPath("eternity", "eternal_effect_projectile")),
                shooter, level);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide && result.getEntity() instanceof LivingEntity target) {
            ItemStack stack = this.getItem();
            if (stack.getItem() instanceof EternalThrowableEffectItem throwable) {
                MobEffectInstance instance = new MobEffectInstance(
                        throwable.getEffect(), EFFECT_DURATION, throwable.getAmplifier());
                target.addEffect(instance);
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        // Skip ThrowableItemProjectile.onHit() which spawns the item as a drop
        if (result.getType() == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult) result);
        } else if (result.getType() == HitResult.Type.BLOCK) {
            this.onHitBlock((BlockHitResult) result);
        }
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SNOWBALL;
    }
}
