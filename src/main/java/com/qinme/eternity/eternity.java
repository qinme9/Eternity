package com.qinme.eternity;

import java.util.List;
import java.util.Random;

import com.qinme.eternity.entity.EternalEffectProjectile;
import com.qinme.eternity.item.EternalEffectFoodItem;
import com.qinme.eternity.item.EternalExpBottleItem;
import com.qinme.eternity.item.EternalFoodItem;
import com.qinme.eternity.item.EternalLingeringPotionItem;
import com.qinme.eternity.item.EternalPotionItem;
import com.qinme.eternity.item.EternalSplashPotionItem;
import com.qinme.eternity.item.EternalThrowableEffectItem;
import com.qinme.eternity.item.EternalThrowableItem;
import com.qinme.eternity.item.EternalTotemItem;
import com.qinme.eternity.block.EternalAnvilBlock;
import com.qinme.eternity.block.EternalAnvilMenu;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(eternity.MODID)
public class eternity {
    public static final String MODID = "eternity";

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
  public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    // === 实体 ===

    public static final DeferredHolder<EntityType<?>, EntityType<EternalEffectProjectile>> ETERNAL_EFFECT_PROJECTILE =
            ENTITY_TYPES.register("eternal_effect_projectile",
                    () -> EntityType.Builder.<EternalEffectProjectile>of(EternalEffectProjectile::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("eternal_effect_projectile"));
    // === 属性辅助方法 ===

    private static Item.Properties eternalFood(int nutrition, float saturation) {
        return new Item.Properties()
                .food(new FoodProperties.Builder()
                        .alwaysEdible()
                        .nutrition(nutrition)
                        .saturationModifier(saturation)
                        .build())
                .stacksTo(1)
                .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
    }

    private static Item.Properties eternalEffectFood() {
        return new Item.Properties()
                .food(new FoodProperties.Builder()
                        .alwaysEdible()
                        .nutrition(1)
                        .saturationModifier(0.1f)
                        .build())
                .stacksTo(1)
                .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
    }

    private static Item.Properties eternalThrowableEffect() {
        return new Item.Properties()
                .stacksTo(1)
                .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
    }

    private static Item.Properties eternalThrowable() {
        return new Item.Properties()
                .stacksTo(1)
                .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
    }

    // === 永恒食物 ===

    public static final DeferredItem<EternalFoodItem> ETERNAL_APPLE = ITEMS.register("eternal_apple",
            () -> new EternalFoodItem(eternalFood(4, 0.3f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_BAKED_POTATO = ITEMS.register("eternal_baked_potato",
            () -> new EternalFoodItem(eternalFood(5, 0.6f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_BEEF = ITEMS.register("eternal_beef",
            () -> new EternalFoodItem(eternalFood(3, 0.3f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_BEETROOT = ITEMS.register("eternal_beetroot",
            () -> new EternalFoodItem(eternalFood(1, 1.2f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_BEETROOT_SOUP = ITEMS.register("eternal_beetroot_soup",
            () -> new EternalFoodItem(eternalFood(6, 0.6f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_BREAD = ITEMS.register("eternal_bread",
            () -> new EternalFoodItem(eternalFood(5, 0.6f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_CARROT = ITEMS.register("eternal_carrot",
            () -> new EternalFoodItem(eternalFood(3, 0.6f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_CHICKEN = ITEMS.register("eternal_chicken",
            () -> new EternalFoodItem(eternalFood(2, 0.3f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_CHORUS_FRUIT = ITEMS.register("eternal_chorus_fruit",
            () -> new EternalFoodItem(eternalFood(4, 0.3f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_COD = ITEMS.register("eternal_cod",
            () -> new EternalFoodItem(eternalFood(2, 0.1f)));

    public static final DeferredItem<EternalFoodItem> ETERNAL_COOKED_BEEF = ITEMS.register("eternal_cooked_beef",
            () -> new EternalFoodItem(eternalFood(8, 0.8f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_COOKED_CHICKEN = ITEMS.register("eternal_cooked_chicken",
            () -> new EternalFoodItem(eternalFood(6, 0.6f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_COOKED_COD = ITEMS.register("eternal_cooked_cod",
            () -> new EternalFoodItem(eternalFood(5, 0.6f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_COOKED_MUTTON = ITEMS.register("eternal_cooked_mutton",
            () -> new EternalFoodItem(eternalFood(6, 0.8f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_COOKED_PORKCHOP = ITEMS.register("eternal_cooked_porkchop",
            () -> new EternalFoodItem(eternalFood(8, 0.8f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_COOKED_RABBIT = ITEMS.register("eternal_cooked_rabbit",
            () -> new EternalFoodItem(eternalFood(5, 0.6f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_COOKED_SALMON = ITEMS.register("eternal_cooked_salmon",
            () -> new EternalFoodItem(eternalFood(6, 0.8f)));

    public static final DeferredItem<EternalFoodItem> ETERNAL_COOKIE = ITEMS.register("eternal_cookie",
            () -> new EternalFoodItem(eternalFood(2, 0.1f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_DRIED_KELP = ITEMS.register("eternal_dried_kelp",
            () -> new EternalFoodItem(eternalFood(1, 0.3f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_GLOW_BERRIES = ITEMS.register("eternal_glow_berries",
            () -> new EternalFoodItem(eternalFood(2, 0.1f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_GOLDEN_APPLE = ITEMS.register("eternal_golden_apple",
            () -> new EternalFoodItem(eternalFood(4, 1.2f)));

    private static final FoodProperties ENCHANTED_GOLDEN_APPLE_FOOD = new FoodProperties.Builder()
            .alwaysEdible()
            .nutrition(4).saturationModifier(1.2f)
            .effect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1), 1.0f)
            .effect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3), 1.0f)
            .effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0), 1.0f)
            .effect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0), 1.0f)
            .build();

    public static final DeferredItem<EternalFoodItem> ETERNAL_ENCHANTED_GOLDEN_APPLE = ITEMS.register("eternal_enchanted_golden_apple",
            () -> new EternalFoodItem(new Item.Properties()
                    .food(ENCHANTED_GOLDEN_APPLE_FOOD)
                    .stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
                    tooltipComponents.add(Component.literal("生命恢复 II (0:20)").withStyle(ChatFormatting.BLUE));
                    tooltipComponents.add(Component.literal("伤害吸收 IV (2:00)").withStyle(ChatFormatting.BLUE));
                    tooltipComponents.add(Component.literal("抗性提升 (5:00)").withStyle(ChatFormatting.BLUE));
                    tooltipComponents.add(Component.literal("抗火 (5:00)").withStyle(ChatFormatting.BLUE));
                }
            });

    public static final DeferredItem<EternalFoodItem> ETERNAL_GOLDEN_CARROT = ITEMS.register("eternal_golden_carrot",
            () -> new EternalFoodItem(eternalFood(6, 1.2f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_HONEY_BOTTLE = ITEMS.register("eternal_honey_bottle",
            () -> new EternalFoodItem(eternalFood(6, 0.1f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_MELON_SLICE = ITEMS.register("eternal_melon_slice",
            () -> new EternalFoodItem(eternalFood(2, 0.3f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_MILK_BUCKET = ITEMS.register("eternal_milk_bucket",
            () -> new EternalFoodItem(eternalFood(1, 0.1f)) {
                @Override
                public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
                    if (!level.isClientSide) {
                        entity.removeAllEffects();
                    }
                    return super.finishUsingItem(stack, level, entity);
                }
            });
    public static final DeferredItem<EternalFoodItem> ETERNAL_MUSHROOM_STEW = ITEMS.register("eternal_mushroom_stew",
            () -> new EternalFoodItem(eternalFood(6, 0.6f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_MUTTON = ITEMS.register("eternal_mutton",
            () -> new EternalFoodItem(eternalFood(2, 0.3f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_POPPED_CHORUS_FRUIT = ITEMS.register("eternal_popped_chorus_fruit",
            () -> new EternalFoodItem(eternalFood(2, 0.1f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_POISONOUS_POTATO = ITEMS.register("eternal_poisonous_potato",
            () -> new EternalFoodItem(eternalFood(2, 0.3f)) {
                @Override
                public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
                    if (!level.isClientSide && entity.getRandom().nextFloat() < 0.6f) {
                        entity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
                    }
                    return super.finishUsingItem(stack, level, entity);
                }
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
                    tooltipComponents.add(Component.literal("60% 概率 中毒 (0:05)").withStyle(ChatFormatting.BLUE));
                }
            });
    public static final DeferredItem<EternalFoodItem> ETERNAL_PORKCHOP = ITEMS.register("eternal_porkchop",
            () -> new EternalFoodItem(eternalFood(3, 0.3f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_PUFFERFISH = ITEMS.register("eternal_pufferfish",
            () -> new EternalFoodItem(eternalFood(1, 0.1f)) {
                @Override
                public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
                    if (!level.isClientSide) {
                        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 300, 0));
                        entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 300, 0));
                        entity.addEffect(new MobEffectInstance(MobEffects.POISON, 1200, 1));
                    }
                    return super.finishUsingItem(stack, level, entity);
                }
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
                    tooltipComponents.add(Component.literal("反胃 (0:15)").withStyle(ChatFormatting.BLUE));
                    tooltipComponents.add(Component.literal("饥饿 (0:15)").withStyle(ChatFormatting.BLUE));
                    tooltipComponents.add(Component.literal("中毒 II (1:00)").withStyle(ChatFormatting.BLUE));
                }
            });
    public static final DeferredItem<EternalFoodItem> ETERNAL_POTATO = ITEMS.register("eternal_potato",
            () -> new EternalFoodItem(eternalFood(1, 0.3f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_PUMPKIN_PIE = ITEMS.register("eternal_pumpkin_pie",
            () -> new EternalFoodItem(eternalFood(8, 0.3f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_RABBIT = ITEMS.register("eternal_rabbit",
            () -> new EternalFoodItem(eternalFood(3, 0.3f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_RABBIT_STEW = ITEMS.register("eternal_rabbit_stew",
            () -> new EternalFoodItem(eternalFood(10, 0.6f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_ROTTEN_FLESH = ITEMS.register("eternal_rotten_flesh",
            () -> new EternalFoodItem(eternalFood(4, 0.1f)) {
                @Override
                public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
                    if (!level.isClientSide && entity.getRandom().nextFloat() < 0.8f) {
                        entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0));
                    }
                    return super.finishUsingItem(stack, level, entity);
                }
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
                    tooltipComponents.add(Component.literal("80% 概率 饥饿 (0:30)").withStyle(ChatFormatting.BLUE));
                }
            });
    public static final DeferredItem<EternalFoodItem> ETERNAL_SALMON = ITEMS.register("eternal_salmon",
            () -> new EternalFoodItem(eternalFood(2, 0.1f)));

    private static final Random STEW_RANDOM = new Random();
    private static final List<MobEffectInstance> STEW_EFFECTS = List.of(
            new MobEffectInstance(MobEffects.REGENERATION, 140, 0),
            new MobEffectInstance(MobEffects.NIGHT_VISION, 100, 0),
            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0),
            new MobEffectInstance(MobEffects.JUMP, 100, 0),
            new MobEffectInstance(MobEffects.SATURATION, 7, 0),
            new MobEffectInstance(MobEffects.BLINDNESS, 160, 0),
            new MobEffectInstance(MobEffects.WEAKNESS, 180, 0),
            new MobEffectInstance(MobEffects.POISON, 240, 0),
            new MobEffectInstance(MobEffects.WITHER, 160, 0)
    );

    public static final DeferredItem<EternalFoodItem> ETERNAL_SUSPICIOUS_STEW = ITEMS.register("eternal_suspicious_stew",
            () -> new EternalFoodItem(eternalFood(6, 0.6f)) {
                @Override
                public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
                    if (!level.isClientSide) {
                        MobEffectInstance randomEffect = STEW_EFFECTS.get(STEW_RANDOM.nextInt(STEW_EFFECTS.size()));
                        entity.addEffect(new MobEffectInstance(randomEffect));
                    }
                    return super.finishUsingItem(stack, level, entity);
                }
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
                    tooltipComponents.add(Component.literal("食用后获得随机效果").withStyle(ChatFormatting.BLUE));
                }
            });
    public static final DeferredItem<EternalFoodItem> ETERNAL_SWEET_BERRIES = ITEMS.register("eternal_sweet_berries",
            () -> new EternalFoodItem(eternalFood(2, 0.1f)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_SPIDER_EYE = ITEMS.register("eternal_spider_eye",
            () -> new EternalFoodItem(eternalFood(2, 0.8f)) {
                @Override
                public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
                    if (!level.isClientSide) {
                        entity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
                    }
                    return super.finishUsingItem(stack, level, entity);
                }
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
                    tooltipComponents.add(Component.literal("中毒 (0:05)").withStyle(ChatFormatting.BLUE));
                }
            });
    public static final DeferredItem<EternalTotemItem> ETERNAL_TOTEM_OF_UNDYING = ITEMS.register("eternal_totem_of_undying",
            () -> new EternalTotemItem(new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
    public static final DeferredItem<EternalFoodItem> ETERNAL_TROPICAL_FISH = ITEMS.register("eternal_tropical_fish",
            () -> new EternalFoodItem(eternalFood(1, 0.1f)));

    // === 永恒效果食物（正面-食用型） ===
    // 负面效果改为投掷型，见下方 === 永恒投掷效果 ===

    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_SPEED = ITEMS.register("eternal_effect_speed",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.MOVEMENT_SPEED));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_HASTE = ITEMS.register("eternal_effect_haste",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.DIG_SPEED));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_STRENGTH = ITEMS.register("eternal_effect_strength",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.DAMAGE_BOOST));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_INSTANT_HEALTH = ITEMS.register("eternal_effect_instant_health",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.HEAL));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_JUMP_BOOST = ITEMS.register("eternal_effect_jump_boost",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.JUMP));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_REGENERATION = ITEMS.register("eternal_effect_regeneration",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.REGENERATION));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_RESISTANCE = ITEMS.register("eternal_effect_resistance",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.DAMAGE_RESISTANCE));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_FIRE_RESISTANCE = ITEMS.register("eternal_effect_fire_resistance",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.FIRE_RESISTANCE));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_WATER_BREATHING = ITEMS.register("eternal_effect_water_breathing",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.WATER_BREATHING));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_INVISIBILITY = ITEMS.register("eternal_effect_invisibility",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.INVISIBILITY));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_NIGHT_VISION = ITEMS.register("eternal_effect_night_vision",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.NIGHT_VISION));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_HEALTH_BOOST = ITEMS.register("eternal_effect_health_boost",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.HEALTH_BOOST));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_ABSORPTION = ITEMS.register("eternal_effect_absorption",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.ABSORPTION));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_SATURATION = ITEMS.register("eternal_effect_saturation",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.SATURATION));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_GLOWING = ITEMS.register("eternal_effect_glowing",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.GLOWING));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_LUCK = ITEMS.register("eternal_effect_luck",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.LUCK));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_SLOW_FALLING = ITEMS.register("eternal_effect_slow_falling",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.SLOW_FALLING));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_CONDUIT_POWER = ITEMS.register("eternal_effect_conduit_power",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.CONDUIT_POWER));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_DOLPHINS_GRACE = ITEMS.register("eternal_effect_dolphins_grace",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.DOLPHINS_GRACE));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_HERO_OF_THE_VILLAGE = ITEMS.register("eternal_effect_hero_of_the_village",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.HERO_OF_THE_VILLAGE));

    // === 永恒投掷效果（负面） ===

    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_SLOWNESS = ITEMS.register("eternal_effect_slowness",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.MOVEMENT_SLOWDOWN));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_MINING_FATIGUE = ITEMS.register("eternal_effect_mining_fatigue",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.DIG_SLOWDOWN));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_INSTANT_DAMAGE = ITEMS.register("eternal_effect_instant_damage",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.HARM));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_NAUSEA = ITEMS.register("eternal_effect_nausea",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.CONFUSION));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_BLINDNESS = ITEMS.register("eternal_effect_blindness",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.BLINDNESS));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_HUNGER = ITEMS.register("eternal_effect_hunger",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.HUNGER));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_WEAKNESS = ITEMS.register("eternal_effect_weakness",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.WEAKNESS));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_POISON = ITEMS.register("eternal_effect_poison",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.POISON));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_WITHER = ITEMS.register("eternal_effect_wither",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.WITHER));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_LEVITATION = ITEMS.register("eternal_effect_levitation",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.LEVITATION));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_UNLUCK = ITEMS.register("eternal_effect_unluck",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.UNLUCK));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_DARKNESS = ITEMS.register("eternal_effect_darkness",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.DARKNESS));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_BAD_OMEN = ITEMS.register("eternal_effect_bad_omen",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.BAD_OMEN));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_RAID_OMEN = ITEMS.register("eternal_effect_raid_omen",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.RAID_OMEN));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_TRIAL_OMEN = ITEMS.register("eternal_effect_trial_omen",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.TRIAL_OMEN));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_WIND_CHARGED = ITEMS.register("eternal_effect_wind_charged",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.WIND_CHARGED));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_WEAVING = ITEMS.register("eternal_effect_weaving",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.WEAVING));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_OOZING = ITEMS.register("eternal_effect_oozing",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.OOZING));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_INFESTED = ITEMS.register("eternal_effect_infested",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.INFESTED));

    // === 永恒效果食物 II 级（正面） ===

    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_SPEED_2 = ITEMS.register("eternal_effect_speed_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.MOVEMENT_SPEED, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_HASTE_2 = ITEMS.register("eternal_effect_haste_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.DIG_SPEED, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_STRENGTH_2 = ITEMS.register("eternal_effect_strength_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.DAMAGE_BOOST, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_INSTANT_HEALTH_2 = ITEMS.register("eternal_effect_instant_health_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.HEAL, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_JUMP_BOOST_2 = ITEMS.register("eternal_effect_jump_boost_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.JUMP, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_REGENERATION_2 = ITEMS.register("eternal_effect_regeneration_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.REGENERATION, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_RESISTANCE_2 = ITEMS.register("eternal_effect_resistance_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.DAMAGE_RESISTANCE, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_FIRE_RESISTANCE_2 = ITEMS.register("eternal_effect_fire_resistance_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.FIRE_RESISTANCE, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_WATER_BREATHING_2 = ITEMS.register("eternal_effect_water_breathing_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.WATER_BREATHING, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_INVISIBILITY_2 = ITEMS.register("eternal_effect_invisibility_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.INVISIBILITY, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_NIGHT_VISION_2 = ITEMS.register("eternal_effect_night_vision_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.NIGHT_VISION, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_HEALTH_BOOST_2 = ITEMS.register("eternal_effect_health_boost_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.HEALTH_BOOST, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_ABSORPTION_2 = ITEMS.register("eternal_effect_absorption_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.ABSORPTION, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_SATURATION_2 = ITEMS.register("eternal_effect_saturation_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.SATURATION, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_GLOWING_2 = ITEMS.register("eternal_effect_glowing_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.GLOWING, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_LUCK_2 = ITEMS.register("eternal_effect_luck_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.LUCK, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_SLOW_FALLING_2 = ITEMS.register("eternal_effect_slow_falling_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.SLOW_FALLING, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_CONDUIT_POWER_2 = ITEMS.register("eternal_effect_conduit_power_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.CONDUIT_POWER, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_DOLPHINS_GRACE_2 = ITEMS.register("eternal_effect_dolphins_grace_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.DOLPHINS_GRACE, 1));
    public static final DeferredItem<EternalEffectFoodItem> ETERNAL_EFFECT_HERO_OF_THE_VILLAGE_2 = ITEMS.register("eternal_effect_hero_of_the_village_2",
            () -> new EternalEffectFoodItem(eternalEffectFood(), MobEffects.HERO_OF_THE_VILLAGE, 1));

    // === 永恒投掷效果 II 级（负面） ===

    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_SLOWNESS_2 = ITEMS.register("eternal_effect_slowness_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.MOVEMENT_SLOWDOWN, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_MINING_FATIGUE_2 = ITEMS.register("eternal_effect_mining_fatigue_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.DIG_SLOWDOWN, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_INSTANT_DAMAGE_2 = ITEMS.register("eternal_effect_instant_damage_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.HARM, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_NAUSEA_2 = ITEMS.register("eternal_effect_nausea_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.CONFUSION, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_BLINDNESS_2 = ITEMS.register("eternal_effect_blindness_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.BLINDNESS, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_HUNGER_2 = ITEMS.register("eternal_effect_hunger_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.HUNGER, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_WEAKNESS_2 = ITEMS.register("eternal_effect_weakness_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.WEAKNESS, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_POISON_2 = ITEMS.register("eternal_effect_poison_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.POISON, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_WITHER_2 = ITEMS.register("eternal_effect_wither_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.WITHER, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_LEVITATION_2 = ITEMS.register("eternal_effect_levitation_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.LEVITATION, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_UNLUCK_2 = ITEMS.register("eternal_effect_unluck_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.UNLUCK, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_DARKNESS_2 = ITEMS.register("eternal_effect_darkness_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.DARKNESS, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_BAD_OMEN_2 = ITEMS.register("eternal_effect_bad_omen_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.BAD_OMEN, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_RAID_OMEN_2 = ITEMS.register("eternal_effect_raid_omen_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.RAID_OMEN, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_TRIAL_OMEN_2 = ITEMS.register("eternal_effect_trial_omen_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.TRIAL_OMEN, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_WIND_CHARGED_2 = ITEMS.register("eternal_effect_wind_charged_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.WIND_CHARGED, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_WEAVING_2 = ITEMS.register("eternal_effect_weaving_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.WEAVING, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_OOZING_2 = ITEMS.register("eternal_effect_oozing_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.OOZING, 1));
    public static final DeferredItem<EternalThrowableEffectItem> ETERNAL_EFFECT_INFESTED_2 = ITEMS.register("eternal_effect_infested_2",
            () -> new EternalThrowableEffectItem(eternalThrowableEffect(), MobEffects.INFESTED, 1));

    // === 永恒药水 ===

    public static final DeferredItem<EternalPotionItem> ETERNAL_POTION = ITEMS.register("eternal_potion",
            () -> new EternalPotionItem(new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

    public static final DeferredItem<EternalSplashPotionItem> ETERNAL_SPLASH_POTION = ITEMS.register("eternal_splash_potion",
            () -> new EternalSplashPotionItem(new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

    public static final DeferredItem<EternalLingeringPotionItem> ETERNAL_LINGERING_POTION = ITEMS.register("eternal_lingering_potion",
            () -> new EternalLingeringPotionItem(new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

    public static final DeferredItem<EternalExpBottleItem> ETERNAL_EXP_BOTTLE = ITEMS.register("eternal_exp_bottle",
            () -> new EternalExpBottleItem(new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

    // === 永恒投掷物 ===

    public static final DeferredItem<EternalThrowableItem> ETERNAL_SNOWBALL = ITEMS.register("eternal_snowball",
            () -> new EternalThrowableItem(eternalThrowable(),
                    (level, player) -> {
                        // 与原版 SnowballItem.use 完全一致
                        var e = new net.minecraft.world.entity.projectile.Snowball(level, player);
                        e.setItem(player.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND).copy());
                        e.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                        return e;
                    }, SoundEvents.SNOWBALL_THROW));
    public static final DeferredItem<EternalThrowableItem> ETERNAL_EGG = ITEMS.register("eternal_egg",
            () -> new EternalThrowableItem(eternalThrowable(),
                    (level, player) -> {
                        // 与原版 ThrownEgg 构造一致
                        var e = new net.minecraft.world.entity.projectile.ThrownEgg(level, player);
                        e.setItem(player.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND).copy());
                        e.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                        return e;
                    }, SoundEvents.EGG_THROW));
    public static final DeferredItem<EternalThrowableItem> ETERNAL_ENDER_PEARL = ITEMS.register("eternal_ender_pearl",
            () -> new EternalThrowableItem(eternalThrowable(),
                    (level, player) -> {
                        // 与原版 EnderpearlItem.use 完全一致
                        var e = new net.minecraft.world.entity.projectile.ThrownEnderpearl(level, player);
                        e.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                        return e;
                    }, SoundEvents.ENDER_PEARL_THROW));
    public static final DeferredItem<EternalThrowableItem> ETERNAL_EYE_OF_ENDER = ITEMS.register("eternal_eye_of_ender",
            () -> new EternalThrowableItem(eternalThrowable(),
                    (level, player) -> {
                        // 使用原版 EyeOfEnder，不设置自定义 item 避免破碎检测失败
                        var e = new net.minecraft.world.entity.projectile.EyeOfEnder(level, player.getX(), player.getY(0.5D), player.getZ());
                        // 不调用 setItem() — 让 entity 使用默认的 Items.ENDER_EYE，保证原版破碎逻辑正确
                        if (level instanceof net.minecraft.server.level.ServerLevel sl) {
                            var bp = sl.findNearestMapStructure(net.minecraft.tags.StructureTags.EYE_OF_ENDER_LOCATED, player.blockPosition(), 100, false);
                            if (bp != null) e.signalTo(bp);
                        }
                        return e;
                    }, SoundEvents.ENDER_PEARL_THROW));
    public static final DeferredItem<EternalThrowableItem> ETERNAL_FIREWORK_ROCKET = ITEMS.register("eternal_firework_rocket",
            () -> new EternalThrowableItem(eternalThrowable(),
                    (level, player) -> {
                        ItemStack fwStack = new ItemStack(Items.FIREWORK_ROCKET);
                        it.unimi.dsi.fastutil.ints.IntArrayList colors = new it.unimi.dsi.fastutil.ints.IntArrayList();
                        colors.add(0xFFFFFF);
                        it.unimi.dsi.fastutil.ints.IntArrayList fades = new it.unimi.dsi.fastutil.ints.IntArrayList();
                        fwStack.set(DataComponents.FIREWORKS, new net.minecraft.world.item.component.Fireworks(
                                1,
                                List.of(new net.minecraft.world.item.component.FireworkExplosion(
                                        net.minecraft.world.item.component.FireworkExplosion.Shape.SMALL_BALL,
                                        colors, fades, false, false
                                ))
                        ));
                        var e = new net.minecraft.world.entity.projectile.FireworkRocketEntity(level, fwStack, player);
                        return e;
                    }, SoundEvents.FIREWORK_ROCKET_LAUNCH));
    public static final DeferredItem<EternalThrowableItem> ETERNAL_WIND_CHARGE = ITEMS.register("eternal_wind_charge",
            () -> new EternalThrowableItem(eternalThrowable(),
                    (level, player) -> {
                        net.minecraft.world.entity.projectile.windcharge.WindCharge e = new net.minecraft.world.entity.projectile.windcharge.WindCharge(EntityType.WIND_CHARGE, level);
                        e.setOwner(player);
                        e.setPos(player.getX(), player.getEyeY() - 0.1D, player.getZ());
                        e.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                        return e;
                    }, SoundEvents.WIND_CHARGE_THROW));

    private static final List<Holder<Potion>> ALL_POTIONS = List.of(
            Potions.NIGHT_VISION,
            Potions.INVISIBILITY,
            Potions.LEAPING, Potions.STRONG_LEAPING,
            Potions.FIRE_RESISTANCE,
            Potions.SWIFTNESS, Potions.STRONG_SWIFTNESS,
            Potions.SLOWNESS, Potions.STRONG_SLOWNESS,
            Potions.TURTLE_MASTER, Potions.STRONG_TURTLE_MASTER,
            Potions.WATER_BREATHING,
            Potions.HEALING, Potions.STRONG_HEALING,
            Potions.HARMING, Potions.STRONG_HARMING,
            Potions.POISON, Potions.STRONG_POISON,
            Potions.REGENERATION, Potions.STRONG_REGENERATION,
            Potions.STRENGTH, Potions.STRONG_STRENGTH,
            Potions.WEAKNESS,
            Potions.SLOW_FALLING,
            Potions.LUCK,
            Potions.WIND_CHARGED, Potions.WEAVING, Potions.OOZING, Potions.INFESTED
    );

    private static void addPotionStacks(CreativeModeTab.Output output) {
        for (Holder<Potion> potion : ALL_POTIONS) {
            PotionContents contents = new PotionContents(potion);
            ItemStack drink = new ItemStack(ETERNAL_POTION.get());
            drink.set(DataComponents.POTION_CONTENTS, contents);
            output.accept(drink);
            ItemStack splash = new ItemStack(ETERNAL_SPLASH_POTION.get());
            splash.set(DataComponents.POTION_CONTENTS, contents);
            output.accept(splash);
            ItemStack lingering = new ItemStack(ETERNAL_LINGERING_POTION.get());
            lingering.set(DataComponents.POTION_CONTENTS, contents);
            output.accept(lingering);
        }
    }

    // === 永恒铁砧 ===

    public static final DeferredBlock<Block> ETERNAL_ANVIL = BLOCKS.register("eternal_anvil",
            () -> new EternalAnvilBlock(BlockBehaviour.Properties.of()
                    .strength(5.0f, 1200.0f)
                    .requiresCorrectToolForDrops()));

    public static final DeferredItem<BlockItem> ETERNAL_ANVIL_ITEM = ITEMS.register("eternal_anvil",
            () -> new BlockItem(ETERNAL_ANVIL.get(), new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

    public static final DeferredHolder<MenuType<?>, MenuType<EternalAnvilMenu>> ETERNAL_ANVIL_MENU = MENU_TYPES.register("eternal_anvil",
            () -> new MenuType<>((windowId, inv) -> new EternalAnvilMenu(windowId, inv, ContainerLevelAccess.NULL), FeatureFlags.DEFAULT_FLAGS));

    // === 永恒食物列表 ===
    private static final DeferredItem<?>[] ALL_ETERNAL_FOODS = {
            ETERNAL_APPLE, ETERNAL_BAKED_POTATO, ETERNAL_BEEF, ETERNAL_BEETROOT,
            ETERNAL_BEETROOT_SOUP, ETERNAL_BREAD, ETERNAL_CARROT, ETERNAL_CHICKEN,
            ETERNAL_CHORUS_FRUIT, ETERNAL_COD,
            ETERNAL_COOKED_BEEF, ETERNAL_COOKED_CHICKEN, ETERNAL_COOKED_COD,
            ETERNAL_COOKED_MUTTON, ETERNAL_COOKED_PORKCHOP, ETERNAL_COOKED_RABBIT,
            ETERNAL_COOKED_SALMON,
            ETERNAL_COOKIE, ETERNAL_DRIED_KELP, ETERNAL_GLOW_BERRIES, ETERNAL_GOLDEN_APPLE,
            ETERNAL_ENCHANTED_GOLDEN_APPLE,
            ETERNAL_GOLDEN_CARROT, ETERNAL_HONEY_BOTTLE, ETERNAL_MELON_SLICE,
            ETERNAL_MILK_BUCKET, ETERNAL_MUSHROOM_STEW, ETERNAL_MUTTON, ETERNAL_POPPED_CHORUS_FRUIT,
            ETERNAL_POISONOUS_POTATO, ETERNAL_PORKCHOP, ETERNAL_PUFFERFISH, ETERNAL_POTATO, ETERNAL_PUMPKIN_PIE,
            ETERNAL_RABBIT, ETERNAL_RABBIT_STEW, ETERNAL_ROTTEN_FLESH,
            ETERNAL_SALMON, ETERNAL_SUSPICIOUS_STEW, ETERNAL_SWEET_BERRIES,
            ETERNAL_SPIDER_EYE,
            ETERNAL_TOTEM_OF_UNDYING, ETERNAL_TROPICAL_FISH,
            ETERNAL_EFFECT_SPEED, ETERNAL_EFFECT_SLOWNESS, ETERNAL_EFFECT_HASTE, ETERNAL_EFFECT_MINING_FATIGUE,
            ETERNAL_EFFECT_STRENGTH, ETERNAL_EFFECT_INSTANT_HEALTH, ETERNAL_EFFECT_INSTANT_DAMAGE,
            ETERNAL_EFFECT_JUMP_BOOST, ETERNAL_EFFECT_NAUSEA, ETERNAL_EFFECT_REGENERATION,
            ETERNAL_EFFECT_RESISTANCE, ETERNAL_EFFECT_FIRE_RESISTANCE, ETERNAL_EFFECT_WATER_BREATHING,
            ETERNAL_EFFECT_INVISIBILITY, ETERNAL_EFFECT_BLINDNESS, ETERNAL_EFFECT_NIGHT_VISION,
            ETERNAL_EFFECT_HUNGER, ETERNAL_EFFECT_WEAKNESS, ETERNAL_EFFECT_POISON,
            ETERNAL_EFFECT_WITHER, ETERNAL_EFFECT_HEALTH_BOOST, ETERNAL_EFFECT_ABSORPTION,
            ETERNAL_EFFECT_SATURATION, ETERNAL_EFFECT_GLOWING, ETERNAL_EFFECT_LEVITATION,
            ETERNAL_EFFECT_LUCK, ETERNAL_EFFECT_UNLUCK, ETERNAL_EFFECT_SLOW_FALLING,
            ETERNAL_EFFECT_CONDUIT_POWER, ETERNAL_EFFECT_DOLPHINS_GRACE, ETERNAL_EFFECT_DARKNESS,
            ETERNAL_EFFECT_BAD_OMEN, ETERNAL_EFFECT_HERO_OF_THE_VILLAGE, ETERNAL_EFFECT_RAID_OMEN,
            ETERNAL_EFFECT_TRIAL_OMEN, ETERNAL_EFFECT_WIND_CHARGED, ETERNAL_EFFECT_WEAVING,
            ETERNAL_EFFECT_OOZING, ETERNAL_EFFECT_INFESTED,
            ETERNAL_EFFECT_SPEED_2, ETERNAL_EFFECT_SLOWNESS_2, ETERNAL_EFFECT_HASTE_2, ETERNAL_EFFECT_MINING_FATIGUE_2,
            ETERNAL_EFFECT_STRENGTH_2, ETERNAL_EFFECT_INSTANT_HEALTH_2, ETERNAL_EFFECT_INSTANT_DAMAGE_2,
            ETERNAL_EFFECT_JUMP_BOOST_2, ETERNAL_EFFECT_NAUSEA_2, ETERNAL_EFFECT_REGENERATION_2,
            ETERNAL_EFFECT_RESISTANCE_2, ETERNAL_EFFECT_FIRE_RESISTANCE_2, ETERNAL_EFFECT_WATER_BREATHING_2,
            ETERNAL_EFFECT_INVISIBILITY_2, ETERNAL_EFFECT_BLINDNESS_2, ETERNAL_EFFECT_NIGHT_VISION_2,
            ETERNAL_EFFECT_HUNGER_2, ETERNAL_EFFECT_WEAKNESS_2, ETERNAL_EFFECT_POISON_2,
            ETERNAL_EFFECT_WITHER_2, ETERNAL_EFFECT_HEALTH_BOOST_2, ETERNAL_EFFECT_ABSORPTION_2,
            ETERNAL_EFFECT_SATURATION_2, ETERNAL_EFFECT_GLOWING_2, ETERNAL_EFFECT_LEVITATION_2,
            ETERNAL_EFFECT_LUCK_2, ETERNAL_EFFECT_UNLUCK_2, ETERNAL_EFFECT_SLOW_FALLING_2,
            ETERNAL_EFFECT_CONDUIT_POWER_2, ETERNAL_EFFECT_DOLPHINS_GRACE_2, ETERNAL_EFFECT_DARKNESS_2,
            ETERNAL_EFFECT_BAD_OMEN_2, ETERNAL_EFFECT_HERO_OF_THE_VILLAGE_2, ETERNAL_EFFECT_RAID_OMEN_2,
            ETERNAL_EFFECT_TRIAL_OMEN_2, ETERNAL_EFFECT_WIND_CHARGED_2, ETERNAL_EFFECT_WEAVING_2,
            ETERNAL_EFFECT_OOZING_2, ETERNAL_EFFECT_INFESTED_2,
            ETERNAL_SPLASH_POTION, ETERNAL_LINGERING_POTION, ETERNAL_EXP_BOTTLE,
            ETERNAL_SNOWBALL, ETERNAL_EGG, ETERNAL_ENDER_PEARL, ETERNAL_EYE_OF_ENDER,
            ETERNAL_FIREWORK_ROCKET, ETERNAL_WIND_CHARGE
    };

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ETERNAL_FOOD_TAB = CREATIVE_MODE_TABS.register("eternal_food",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.eternity"))
                    .withTabsBefore(CreativeModeTabs.FOOD_AND_DRINKS)
                    .icon(() -> new ItemStack(ETERNAL_GOLDEN_APPLE.get()))
                    .displayItems((parameters, output) -> {
                        for (DeferredItem<?> food : ALL_ETERNAL_FOODS) {
                            output.accept(food.get());
                        }
                        addPotionStacks(output);
                        output.accept(ETERNAL_ANVIL_ITEM.get());
                    }).build());

    public eternity(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
    }
}
