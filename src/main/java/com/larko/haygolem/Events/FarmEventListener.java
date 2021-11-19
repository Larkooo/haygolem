package com.larko.haygolem.Events;

import com.larko.haygolem.Main;
import com.larko.haygolem.Managers.FarmManager;
import com.larko.haygolem.Util.Metadata;
import com.larko.haygolem.World.Farm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid=Metadata.MODID)
public class FarmEventListener {

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if (!(event.getWorld().getTileEntity(event.getPos()) instanceof TileEntitySign))
            return;

        // Check if there is the word farm in the sign's text content
        TileEntitySign sign = (TileEntitySign) event.getWorld().getTileEntity(event.getPos());
        boolean trigger = false;
        for (ITextComponent text : sign.signText)
        {
            if (text.getUnformattedText().contains("farm") || text.getUnformattedText().contains("Farm"))
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
                Main.commonProxy.showFarmGui(farm, event.getWorld(), event.getEntityPlayer());
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
