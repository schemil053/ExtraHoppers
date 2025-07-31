package de.schemil053.extrahoppers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Extrahoppers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final int MIN_HOPPER_SPEED = 1;
    public static final int MAX_HOPPER_SPEED = 64;

    public static final ForgeConfigSpec.IntValue HOPPER_SPEED = BUILDER.defineInRange("hopper-speed", 1, MIN_HOPPER_SPEED, MAX_HOPPER_SPEED);
    public static final ForgeConfigSpec.IntValue HOPPER_PLUS_SPEED = BUILDER.defineInRange("hopper-plus-speed", 4, 1, MAX_HOPPER_SPEED);
    public static final ForgeConfigSpec.IntValue HOPPER_PLUS_PLUS_SPEED = BUILDER.defineInRange("hopper-plus-plus-speed", 8, MIN_HOPPER_SPEED, MAX_HOPPER_SPEED);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int hopperSpeed = 1;
    public static int hopperPlusSpeed = 4;
    public static int hopperPlusPlusSpeed = 8;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        hopperSpeed = HOPPER_SPEED.get();
        hopperPlusSpeed = HOPPER_PLUS_SPEED.get();
        hopperPlusPlusSpeed = HOPPER_PLUS_PLUS_SPEED.get();
    }
}
