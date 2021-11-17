package com.larko.haygolem.Events;

import com.larko.haygolem.Entity.HayGolemEntity;
import com.larko.haygolem.Graphics.Screens.FarmGui;
import com.larko.haygolem.Managers.FarmManager;
import com.larko.haygolem.Util.Metadata;
import com.larko.haygolem.World.Farm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid= Metadata.MODID)
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
            if (text.getUnformattedText().contains("farm"))
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
                Minecraft.getMinecraft().displayGuiScreen(new FarmGui(farm));
                return;
            }
        }
    }
}
