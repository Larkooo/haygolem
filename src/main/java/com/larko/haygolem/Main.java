package com.larko.haygolem;

import com.larko.haygolem.Events.FarmEventListener;
import com.larko.haygolem.Events.HayGolemEventListener;
import com.larko.haygolem.Handlers.RegistryHandler;
import com.larko.haygolem.Managers.GolemManager;
import com.larko.haygolem.Proxy.ClientProxy;
import com.larko.haygolem.Proxy.ServerProxy;
import com.larko.haygolem.Serializers.FarmSerializer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.larko.haygolem.Handlers.PacketHandler;
import com.larko.haygolem.Managers.FarmManager;
import com.larko.haygolem.Proxy.CommonProxy;
import com.larko.haygolem.Util.Metadata;

import net.minecraftforge.fml.common.Mod;

@Mod(Metadata.MODID)
public class Main
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static CommonProxy proxy = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public Main() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        FMLJavaModLoadingContext.get().getModEventBus().register(RegistryHandler.class);
        MinecraftForge.EVENT_BUS.register(FarmManager.class);
        MinecraftForge.EVENT_BUS.register(GolemManager.class);
        MinecraftForge.EVENT_BUS.register(FarmEventListener.class);
        MinecraftForge.EVENT_BUS.register(HayGolemEventListener.class);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // register entities, items and blocks
        RegistryHandler.ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        RegistryHandler.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        RegistryHandler.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        PacketHandler.registerMessages("farmgui");
        Main.proxy.setup();
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        Level world = (Level) event.getWorld();
        if (world instanceof ServerLevel
                && world.dimension().equals(Level.OVERWORLD))
        {
            FarmManager.farms.clear();
            FarmSerializer.get(world.getServer());
            LOGGER.info("farms deserialized");
        }
    }
}
