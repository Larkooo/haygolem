package com.larko.haygolem.Handlers;

import com.larko.haygolem.Entity.HayGolemEntity;
import com.larko.haygolem.Renderers.HayGolemRenderer;
import com.larko.haygolem.Util.Metadata;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderIronGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.DataSerializerEntry;

@Mod.EventBusSubscriber
public class RegistryHandler {

    // gets incremented every time an entity is registered
    public static int entityId = 0;

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        EntityEntry hayGolem = EntityEntryBuilder.create()
                .entity(HayGolemEntity.class)
                .id(new ResourceLocation(Metadata.MODID, "hay_golem"), entityId++)
                .name("hay_golem")
                .egg(0xffffff, 0xffffff)
                .tracker(50, 1, true)
                .build();

        event.getRegistry().register(hayGolem);
    }

    //subscribes to the register event for entities
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        // register haygolem renderer
        RenderingRegistry.registerEntityRenderingHandler(HayGolemEntity.class, HayGolemRenderer::new);
    }


}
