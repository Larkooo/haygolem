package com.larko.haygolem;

import org.apache.logging.log4j.Logger;

import com.larko.haygolem.Handlers.PacketHandler;
import com.larko.haygolem.Managers.FarmManager;
import com.larko.haygolem.Proxy.CommonProxy;
import com.larko.haygolem.Util.Metadata;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = Metadata.MODID, name = Metadata.NAME, version = Metadata.VERSION)
public class Main
{
    @Mod.Instance
    public static Main instance;

    private static Logger logger;

    @SidedProxy(clientSide = Metadata.CLIENT_PROXY, serverSide = Metadata.SERVER_PROXY)
    public static CommonProxy commonProxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	PacketHandler.registerMessages("farmgui");
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        //NetworkRegistry.INSTANCE.registerGuiHandler(Metadata.MODID, );
        logger.info("initialized");
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        FarmManager.deserialize();
    }

    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        FarmManager.serialize();
    }
}
