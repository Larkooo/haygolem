package com.larko.haygolem.Managers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHay;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class GolemManager {


    // spawn a golem if structure
    // has been constructed
    //  - (hay)
    //- - -
    //  -
    @SubscribeEvent
    public void onPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        Block placedBlock = event.getPlacedBlock().getBlock();
        if (!(placedBlock instanceof BlockHay))
            return;


    }
}
