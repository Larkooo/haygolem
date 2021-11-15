package com.larko.haygolem.Managers;

import com.larko.haygolem.Entity.HayGolemEntity;
import com.larko.haygolem.Util.Metadata;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHay;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.pattern.BlockMaterialMatcher;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemEgg;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid=Metadata.MODID)
public class GolemManager {

    public static BlockPattern pattern = FactoryBlockPattern
            .start()
            .aisle("~ ~", "###", "~#~")
            //.where('^', BlockWorldState.hasState(IS_PUMPKIN))
            .where('#', BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.IRON_BLOCK)))
            .where('~', BlockWorldState.hasState(BlockMaterialMatcher.forMaterial(Material.AIR)))
            .build();

    private static ArrayList<HayGolemEntity> entities = new ArrayList<>();
    // spawn a golem if structure
    // has been constructed
    //  - (hay)
    //- - -
    //  -
    @SubscribeEvent
    public static void onPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof EntityPlayerMP))
            return;

        Block placedBlock = event.getPlacedBlock().getBlock();
        Block underBlock = event.getWorld().getBlockState(event.getPos().down()).getBlock();
        World worldIn = event.getWorld();
        // check if block is hay, and if it has been placed on top of an iron block
        // if not, return
        if (!(placedBlock instanceof BlockHay && underBlock.getRegistryName().getResourcePath().equals("iron_block")))
            return;

        // now check for the golem spawn structure pattern
        BlockPattern.PatternHelper patternHelper = pattern.match(event.getWorld(), event.getPos());
        if (patternHelper == null)
            return;

        // replace blocks by air
        for (int j = 0; j < pattern.getPalmLength(); ++j)
        {
            for (int k = 0; k < pattern.getThumbLength(); ++k)
            {
                worldIn.setBlockState(patternHelper.translateOffset(j, k, 0).getPos(), Blocks.AIR.getDefaultState(), 2);
            }
        }

        BlockPos blockpos = patternHelper.translateOffset(1, 2, 0).getPos();
        entities.add(new HayGolemEntity(worldIn));
        entities.get(entities.size() - 1).setLocationAndAngles((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.05D, (double)blockpos.getZ() + 0.5D, 0.0F, 0.0F);
        worldIn.spawnEntity(entities.get(entities.size() - 1));
//        for (EntityPlayerMP entityplayermp1 : worldIn.getEntitiesWithinAABB(EntityPlayerMP.class, hayGolemEntity.getEntityBoundingBox().grow(5.0D)))
//        {
//            CriteriaTriggers.SUMMONED_ENTITY.trigger(entityplayermp1, hayGolemEntity);
//        }

        for (int j1 = 0; j1 < 120; ++j1)
        {
            worldIn.spawnParticle(EnumParticleTypes.SNOWBALL, (double)blockpos.getX() + worldIn.rand.nextDouble(), (double)blockpos.getY() + worldIn.rand.nextDouble() * 3.9D, (double)blockpos.getZ() + worldIn.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
        }

        for (int k1 = 0; k1 < pattern.getPalmLength(); ++k1)
        {
            for (int l1 = 0; l1 < pattern.getThumbLength(); ++l1)
            {
                BlockWorldState blockworldstate1 = patternHelper.translateOffset(k1, l1, 0);
                worldIn.notifyNeighborsRespectDebug(blockworldstate1.getPos(), Blocks.AIR, false);
            }
        }
    }
}
