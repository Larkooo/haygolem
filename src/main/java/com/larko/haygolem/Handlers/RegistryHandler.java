package com.larko.haygolem.Handlers;

import com.larko.haygolem.Block.FarmMarkerBlock;
import com.larko.haygolem.Entity.HayGolemEntity;
import com.larko.haygolem.Graphics.Renderers.HayGolemRenderer;
import com.larko.haygolem.Util.Metadata;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;


@Mod.EventBusSubscriber(modid=Metadata.MODID)
public class RegistryHandler {

    // gets incremented every time an entity is registered
    public static int entityId = 0;

    public static FarmMarkerBlock farmMarkerBlock = new FarmMarkerBlock();

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        EntityEntry hayGolem = EntityEntryBuilder.create()
                .entity(HayGolemEntity.class)
                .id(new ResourceLocation(Metadata.MODID, "hay_golem"), entityId++)
                .name("hay_golem")
                .egg(0xffffff, 0xffffff)
                .tracker(200, 3, true)
                .build();

        event.getRegistry().register(hayGolem);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(farmMarkerBlock);
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(farmMarkerBlock).setRegistryName(farmMarkerBlock.getRegistryName()));
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        NonNullList<Ingredient> lst = NonNullList.create();
        lst.add(Ingredient.fromItem(Item.getItemFromBlock(Blocks.TORCH)));
        lst.add(Ingredient.fromItem(Item.getItemFromBlock(Blocks.TORCH)));
        event.getRegistry().register(new ShapelessRecipes("farm_marker", new ItemStack(Item.getItemFromBlock(farmMarkerBlock)), lst).setRegistryName(Metadata.MODID, "farm_marker_recipe"));
    }

    //subscribes to the register event for entities
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        // register haygolem renderer
        RenderingRegistry.registerEntityRenderingHandler(HayGolemEntity.class, HayGolemRenderer::new);
        System.out.println(new ModelResourceLocation(Item.getItemFromBlock(farmMarkerBlock).getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(farmMarkerBlock), 0, new ModelResourceLocation(Item.getItemFromBlock(farmMarkerBlock).getRegistryName(), "inventory"));
    }


}
