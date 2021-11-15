package com.larko.haygolem.Handlers;

import com.larko.haygolem.Util.Metadata;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid= Metadata.MODID)
public class CapabilitiesHandler {

    @SubscribeEvent
    public static void attachWorldCapabilitiy(AttachCapabilitiesEvent<World> event)
    {

    }
}
