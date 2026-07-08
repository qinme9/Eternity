package com.qinme.eternity.entity;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

public class EternalThrownPotion extends ThrownPotion {

    private final Holder<MobEffect> effect;
    private final int amplifier;

    public EternalThrownPotion(Level level, LivingEntity shooter, Holder<MobEffect> effect, int amplifier) {
        super(level, shooter);
        this.effect = effect;
        this.amplifier = amplifier;
        ItemStack fakeStack = new ItemStack(Items.SPLASH_POTION);
        PotionContents contents = fakeStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        PotionContents custom = contents.withEffectAdded(new MobEffectInstance(effect, 6000, amplifier));
        fakeStack.set(DataComponents.POTION_CONTENTS, custom);
        this.setItem(fakeStack);
    }
}
