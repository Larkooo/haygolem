package com.larko.haygolem.Events;

import com.larko.haygolem.Entity.HayGolemEntity;
import com.larko.haygolem.Main;
import com.larko.haygolem.Managers.FarmManager;
import com.larko.haygolem.Util.Metadata;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Metadata.MODID)
public class HayGolemEventListener {

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.EntityInteract event)
    {
        if (event.getWorld().isClientSide || !(event.getTarget() instanceof HayGolemEntity))
            return;

        HayGolemEntity entity = (HayGolemEntity) event.getTarget();

        //event.getEntityPlayer().openGui(Metadata.MODID, "minecraft:chest");
        //Minecraft.getMinecraft().displayGuiScreen(new HayGolemGui(event.getEntityPlayer().inventory, entity));
        event.getPlayer().openMenu(new SimpleMenuProvider((p_53124_, p_53125_, p_53126_) -> {
            return ChestMenu.threeRows(p_53124_, p_53125_, entity.getInventory());
        }, new TextComponent("HayGolem")));
    }

//    @SubscribeEvent
//    public static void onPlayerMove(TickEvent.PlayerTickEvent event)
//    {
//        //System.out.println(FarmManager.farms.get(0).isWithinBounds(event.player.getPosition()));
//    }
}
