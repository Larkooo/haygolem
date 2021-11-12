package com.larko.haygolem.Golem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHay;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockPumpkin;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
    public void onPlayerInteract(BlockEvent.EntityPlaceEvent event) {
        Block placedBlock = event.getPlacedBlock().getBlock();
        if (!(placedBlock instanceof BlockHay))
            return;

        
    }
}
