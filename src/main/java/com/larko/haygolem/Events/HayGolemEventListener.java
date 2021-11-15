package com.larko.haygolem.Events;

import com.larko.haygolem.Entity.HayGolemEntity;
import com.larko.haygolem.Main;
import com.larko.haygolem.Managers.FarmManager;
import com.larko.haygolem.Util.Metadata;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid=Metadata.MODID)
public class HayGolemEventListener {

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.EntityInteract event)
    {
        if (!(event.getTarget() instanceof HayGolemEntity))
            return;

        HayGolemEntity entity = (HayGolemEntity) event.getTarget();

        //event.getEntityPlayer().openGui(Metadata.MODID, "minecraft:chest");
        //Minecraft.getMinecraft().displayGuiScreen(new HayGolemGui(event.getEntityPlayer().inventory, entity));
        event.getEntityPlayer().displayGUIChest(entity.getInventory());
    }

    @SubscribeEvent
    public static void onPlayerMove(TickEvent.PlayerTickEvent event)
    {
        //System.out.println(FarmManager.farms.get(0).isWithinBounds(event.player.getPosition()));
    }
}
