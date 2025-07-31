package de.schemil053.extrahoppers;

import com.mojang.logging.LogUtils;
import de.schemil053.extrahoppers.block.normal.*;
import de.schemil053.extrahoppers.block.plus.*;
import de.schemil053.extrahoppers.block.plusplus.*;
import de.schemil053.extrahoppers.blockentity.ExtraHopperBlockEntity;
import de.schemil053.extrahoppers.gui.ConfigScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Extrahoppers.MODID)
public class Extrahoppers {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "extrahoppers";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "extrahoppers" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "extrahoppers" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "extrahoppers" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<Block> IRON_HOPPER_PLUS = BLOCKS.register("iron_hopper_plus", IronHopperPlusBlock::new);
    public static final RegistryObject<Item> IRON_HOPPER_PLUS_ITEM = ITEMS.register("iron_hopper_plus", () -> new BlockItem(IRON_HOPPER_PLUS.get(), new Item.Properties()));

    public static final RegistryObject<Block> IRON_HOPPER_PLUS_PLUS = BLOCKS.register("iron_hopper_plus_plus", IronHopperPlusPlusBlock::new);
    public static final RegistryObject<Item> IRON_HOPPER_PLUS_PLUS_ITEM = ITEMS.register("iron_hopper_plus_plus", () -> new BlockItem(IRON_HOPPER_PLUS_PLUS.get(), new Item.Properties()));

    public static final RegistryObject<Block> HONEYED_HOPPER = BLOCKS.register("honey_hopper", HoneyHopperBlock::new);
    public static final RegistryObject<Item> HONEYED_HOPPER_ITEM = ITEMS.register("honey_hopper", () -> new BlockItem(HONEYED_HOPPER.get(), new Item.Properties()));

    public static final RegistryObject<Block> HONEYED_HOPPER_PLUS = BLOCKS.register("honey_hopper_plus", HoneyHopperPlusBlock::new);
    public static final RegistryObject<Item> HONEYED_HOPPER_PLUS_ITEM = ITEMS.register("honey_hopper_plus", () -> new BlockItem(HONEYED_HOPPER_PLUS.get(), new Item.Properties()));

    public static final RegistryObject<Block> HONEYED_HOPPER_PLUS_PLUS = BLOCKS.register("honey_hopper_plus_plus", HoneyHopperPlusPlusBlock::new);
    public static final RegistryObject<Item> HONEYED_HOPPER_PLUS_PLUS_ITEM = ITEMS.register("honey_hopper_plus_plus", () -> new BlockItem(HONEYED_HOPPER_PLUS_PLUS.get(), new Item.Properties()));

    public static final RegistryObject<Block> COPPER_HOPPER = BLOCKS.register("copper_hopper", CopperHopperBlock::new);
    public static final RegistryObject<Item> COPPER_HOPPER_ITEM = ITEMS.register("copper_hopper", () -> new BlockItem(COPPER_HOPPER.get(), new Item.Properties()));

    public static final RegistryObject<Block> COPPER_HOPPER_PLUS = BLOCKS.register("copper_hopper_plus", CopperHopperPlusBlock::new);
    public static final RegistryObject<Item> COPPER_HOPPER_PLUS_ITEM = ITEMS.register("copper_hopper_plus", () -> new BlockItem(COPPER_HOPPER_PLUS.get(), new Item.Properties()));

    public static final RegistryObject<Block> COPPER_HOPPER_PLUS_PLUS = BLOCKS.register("copper_hopper_plus_plus", CopperHopperPlusPlusBlock::new);
    public static final RegistryObject<Item> COPPER_HOPPER_PLUS_PLUS_ITEM = ITEMS.register("copper_hopper_plus_plus", () -> new BlockItem(COPPER_HOPPER_PLUS_PLUS.get(), new Item.Properties()));

    public static final RegistryObject<Block> GOLDEN_HOPPER = BLOCKS.register("golden_hopper", GoldenHopperBlock::new);
    public static final RegistryObject<Item> GOLDEN_HOPPER_ITEM = ITEMS.register("golden_hopper", () -> new BlockItem(GOLDEN_HOPPER.get(), new Item.Properties()));

    public static final RegistryObject<Block> GOLDEN_HOPPER_PLUS = BLOCKS.register("golden_hopper_plus", GoldenHopperPlusBlock::new);
    public static final RegistryObject<Item> GOLDEN_HOPPER_PLUS_ITEM = ITEMS.register("golden_hopper_plus", () -> new BlockItem(GOLDEN_HOPPER_PLUS.get(), new Item.Properties()));

    public static final RegistryObject<Block> GOLDEN_HOPPER_PLUS_PLUS = BLOCKS.register("golden_hopper_plus_plus", GoldenHopperPlusPlusBlock::new);
    public static final RegistryObject<Item> GOLDEN_HOPPER_PLUS_PLUS_ITEM = ITEMS.register("golden_hopper_plus_plus", () -> new BlockItem(GOLDEN_HOPPER_PLUS_PLUS.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIAMOND_HOPPER = BLOCKS.register("diamond_hopper", DiamondHopperBlock::new);
    public static final RegistryObject<Item> DIAMOND_HOPPER_ITEM = ITEMS.register("diamond_hopper", () -> new BlockItem(DIAMOND_HOPPER.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIAMOND_HOPPER_PLUS = BLOCKS.register("diamond_hopper_plus", DiamondHopperPlusBlock::new);
    public static final RegistryObject<Item> DIAMOND_HOPPER_PLUS_ITEM = ITEMS.register("diamond_hopper_plus", () -> new BlockItem(DIAMOND_HOPPER_PLUS.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIAMOND_HOPPER_PLUS_PLUS = BLOCKS.register("diamond_hopper_plus_plus", DiamondHopperPlusPlusBlock::new);
    public static final RegistryObject<Item> DIAMOND_HOPPER_PLUS_PLUS_ITEM = ITEMS.register("diamond_hopper_plus_plus", () -> new BlockItem(DIAMOND_HOPPER_PLUS_PLUS.get(), new Item.Properties()));

    public static final RegistryObject<Block> NETHERITE_HOPPER = BLOCKS.register("netherite_hopper", NetheriteHopperBlock::new);
    public static final RegistryObject<Item> NETHERITE_HOPPER_ITEM = ITEMS.register("netherite_hopper", () -> new BlockItem(NETHERITE_HOPPER.get(), new Item.Properties()));

    public static final RegistryObject<Block> NETHERITE_HOPPER_PLUS = BLOCKS.register("netherite_hopper_plus", NetheriteHopperPlusBlock::new);
    public static final RegistryObject<Item> NETHERITE_HOPPER_PLUS_ITEM = ITEMS.register("netherite_hopper_plus", () -> new BlockItem(NETHERITE_HOPPER_PLUS.get(), new Item.Properties()));

    public static final RegistryObject<Block> NETHERITE_HOPPER_PLUS_PLUS = BLOCKS.register("netherite_hopper_plus_plus", NetheriteHopperPlusPlusBlock::new);
    public static final RegistryObject<Item> NETHERITE_HOPPER_PLUS_PLUS_ITEM = ITEMS.register("netherite_hopper_plus_plus", () -> new BlockItem(NETHERITE_HOPPER_PLUS_PLUS.get(), new Item.Properties()));


    public static final RegistryObject<CreativeModeTab> HOPPER_TAB = CREATIVE_MODE_TABS.register(MODID, () -> {
        return CreativeModeTab.builder()
                .title(Component.translatable("item_group."+MODID))
                .icon(() -> new ItemStack(HONEYED_HOPPER.get()))
                .displayItems((params, output) -> {
                    for (RegistryObject<Item> entry : ITEMS.getEntries()) {
                        output.accept(entry.get());
                    }
                })
                .build();
    });

    public static final RegistryObject<BlockEntityType<ExtraHopperBlockEntity>> HOPPER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("extrahopper_block_entity", () -> BlockEntityType.Builder.of(
                    ExtraHopperBlockEntity::new,
                    HONEYED_HOPPER.get(),
                    DIAMOND_HOPPER.get(),
                    COPPER_HOPPER.get(),
                    GOLDEN_HOPPER.get(),
                    NETHERITE_HOPPER.get(),
                    HONEYED_HOPPER_PLUS.get(),
                    DIAMOND_HOPPER_PLUS.get(),
                    COPPER_HOPPER_PLUS.get(),
                    GOLDEN_HOPPER_PLUS.get(),
                    NETHERITE_HOPPER_PLUS.get(),
                    IRON_HOPPER_PLUS.get(),
                    HONEYED_HOPPER_PLUS_PLUS.get(),
                    DIAMOND_HOPPER_PLUS_PLUS.get(),
                    COPPER_HOPPER_PLUS_PLUS.get(),
                    GOLDEN_HOPPER_PLUS_PLUS.get(),
                    NETHERITE_HOPPER_PLUS_PLUS.get(),
                    IRON_HOPPER_PLUS_PLUS.get()
            ).build(null));

    public static final TagKey<Block> HOPPERS = TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), ResourceLocation.fromNamespaceAndPath("c", "hoppers"));


    public Extrahoppers(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> new ConfigScreen(screen)));
        }

    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
