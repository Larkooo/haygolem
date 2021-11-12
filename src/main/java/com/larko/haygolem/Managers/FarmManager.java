package com.larko.haygolem.Managers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHay;
import net.minecraft.block.BlockTorch;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FarmManager {

    @SubscribeEvent
    public void onPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        Block placedBlock = event.getPlacedBlock().getBlock();
        if (!(placedBlock instanceof BlockTorch))
            return;


    }
}
