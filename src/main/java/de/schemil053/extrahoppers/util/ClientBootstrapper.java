package de.schemil053.extrahoppers.util;


import de.schemil053.extrahoppers.gui.ConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientBootstrapper {
    private final FMLJavaModLoadingContext context;

    public ClientBootstrapper(FMLJavaModLoadingContext context) {
        this.context = context;
    }

    public void bootstrap() {
        context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> new ConfigScreen(screen)));
    }
}
