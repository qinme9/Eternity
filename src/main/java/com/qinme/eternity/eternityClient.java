package com.qinme.eternity;

import static com.qinme.eternity.eternity.ETERNAL_POTION;
import static com.qinme.eternity.eternity.ETERNAL_SPLASH_POTION;
import static com.qinme.eternity.eternity.ETERNAL_LINGERING_POTION;
import static com.qinme.eternity.eternity.ETERNAL_EFFECT_PROJECTILE;

import com.qinme.eternity.entity.EternalEffectProjectile;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = eternity.MODID, dist = Dist.CLIENT)
public class eternityClient {
    public eternityClient(ModContainer container, IEventBus modEventBus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(eternityClient::registerItemColors);
        modEventBus.addListener(eternityClient::registerEntityRenderers);
    }

    private static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                return contents.getColor();
            }
            return -1;
        }, ETERNAL_POTION.get(), ETERNAL_SPLASH_POTION.get(), ETERNAL_LINGERING_POTION.get());
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ETERNAL_EFFECT_PROJECTILE.get(),
                (context) -> new ThrownItemRenderer<>(context, 1.0F, true));
    }
}
