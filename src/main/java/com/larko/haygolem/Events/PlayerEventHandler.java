package com.larko.haygolem.Events;

import com.larko.haygolem.Entity.HayGolemEntity;
import com.larko.haygolem.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class PlayerEventHandler {

    //@SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.EntityInteract event)
    {
        if (!(event.getTarget() instanceof HayGolemEntity))
            return;
        HayGolemEntity entity = (HayGolemEntity) event.getTarget();

        Minecraft.getMinecraft().displayGuiScreen(new GuiInventory(event.getEntityPlayer()));
    }
}
