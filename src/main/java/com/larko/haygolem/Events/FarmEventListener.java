package com.larko.haygolem.Events;

import com.larko.haygolem.Main;
import com.larko.haygolem.Managers.FarmManager;
import com.larko.haygolem.Util.Metadata;
import com.larko.haygolem.World.Farm;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;

@Mod.EventBusSubscriber(modid=Metadata.MODID)
public class FarmEventListener {

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if (!(event.getWorld().getBlockEntity(event.getPos()) instanceof SignBlockEntity))
            return;

        // Check if there is the word farm in the sign's text content
        SignBlockEntity sign = (SignBlockEntity) event.getWorld().getBlockEntity(event.getPos());

        boolean trigger = false;
        for (int i = 0; i < 4; i++)
        {
            String text = sign.getMessage(i, false).getString();

            if (text.toLowerCase().contains("farm"))
            {
                trigger = true;
                break;
            }
        }
        if (!trigger)
            return;

        // Gui
        for (Farm farm : FarmManager.farms)
        {
            if (farm.isWithinBounds(event.getPos()))
            {
                Main.proxy.showFarmGui(farm, event.getWorld(), event.getPlayer());
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onBlockDestroy(BlockEvent.BreakEvent event)
    {
        // check if broke block is boundary block
        if (!(event.getWorld().getBlockState(event.getPos()).getBlock().getClass() == FarmManager.boundaryBlock))
            return;

        // check if is within a farm
        for (int i = 0; i < FarmManager.farms.size(); i++)
        {
            if (FarmManager.farms.get(i).isWithinBounds(event.getPos()))
            {
                // remove the farm
                FarmManager.farms.remove(i);
                return;
            }
        }
    }
}
