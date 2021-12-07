package com.larko.haygolem.Handlers;

import com.larko.haygolem.Block.FarmMarkerBlock;
import com.larko.haygolem.Entity.HayGolemEntity;
import com.larko.haygolem.Graphics.Renderers.HayGolemRenderer;
import com.larko.haygolem.Util.Metadata;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;

import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.http.client.entity.EntityBuilder;

import java.util.ArrayList;
import java.util.List;


@Mod.EventBusSubscriber(modid=Metadata.MODID)
public class RegistryHandler {

    // gets incremented every time an entity is registered
    public static int entityId = 0;

    public static FarmMarkerBlock farmMarkerBlock = (FarmMarkerBlock) new FarmMarkerBlock(
            BlockBehaviour.Properties.of(Material.DECORATION).noCollission().instabreak().lightLevel((p_50886_) -> 14).sound(SoundType.CROP), ParticleTypes.FLAME);

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Metadata.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES,
            Metadata.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            Metadata.MODID);

    public static final RegistryObject<EntityType<HayGolemEntity>> HAY_GOLEM = ENTITY_TYPES.register("hay_golem",
            () -> EntityType.Builder.of(HayGolemEntity::new, MobCategory.MISC).sized(1.4F, 2.7F).clientTrackingRange(10).immuneTo(Blocks.CACTUS).build("hay_golem"));
    public static final RegistryObject<Block> FARM_MARKER_BLOCK = BLOCKS.register("farm_marker", () -> farmMarkerBlock);
    public static final RegistryObject<Item> FARM_MARKER_ITEM = ITEMS.register("farm_marker", () -> new BlockItem(FARM_MARKER_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));

//    @SubscribeEvent
//    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
//        EntityBuilder hayGolem = EntityType.Builder.of()
//                (HayGolemEntity.class)
//                .id(new ResourceLocation(Metadata.MODID, "hay_golem"), entityId++)
//                .name("hay_golem")
//                .egg(0xffffff, 0xffffff)
//                .tracker(200, 3, true)
//                .build();
//
//        event.getRegistry().register(hayGolem);
//    }

//    @SubscribeEvent
//    public static void registerBlocks(RegistryEvent.Register<Block> event) {
//        event.getRegistry().register(farmMarkerBlock);
//    }
//
//    @SubscribeEvent
//    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
//        event.getRegistry().register(new BlockItem(farmMarkerBlock, farmMarkerBloc).setRegistryName(farmMarkerBlock.getRegistryName()));
//    }

    // register attributes
    @SubscribeEvent
    public static void entityAttributCreation(EntityAttributeCreationEvent event) {
        event.put(HAY_GOLEM.get(), HayGolemEntity.createAttributes().build());
    }


    //subscribes to the register event for entities
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerModels(EntityRenderersEvent.RegisterRenderers event) {
        // register haygolem renderer
        event.registerEntityRenderer(HAY_GOLEM.get(), HayGolemRenderer::new);
        //ModelLoaderRegistry.regiregisterEntityRenderingHandler(HayGolemEntity.class, HayGolemRenderer::new);
        //System.out.println(new ModelResourceLocation(Item.getItemFromBlock(farmMarkerBlock).getRegistryName(), "inventory"));
//        ModelLoaderRegistry.reg.register(Item.byBlock(farmMarkerBlock), Rende);
    }


}
