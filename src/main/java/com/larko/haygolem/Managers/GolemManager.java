package com.larko.haygolem.Managers;

import com.larko.haygolem.Entity.HayGolemEntity;
import com.larko.haygolem.Handlers.RegistryHandler;
import com.larko.haygolem.Util.Metadata;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid=Metadata.MODID)
public class GolemManager {

    public static BlockPattern pattern = BlockPatternBuilder
            .start()
            .aisle("~ ~", "###", "~#~")
            //.where('^', BlockWorldState.hasState(IS_PUMPKIN))
            .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK)))
            .where('~', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.AIR)))
            .build();

    private static ArrayList<HayGolemEntity> entities = new ArrayList<>();
    // spawn a golem if structure
    // has been constructed
    //  - (hay)
    //- - -
    //  -
    @SubscribeEvent
    public static void onPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer))
            return;

        Block placedBlock = event.getPlacedBlock().getBlock();
        Block underBlock = event.getWorld().getBlockState(event.getPos().below()).getBlock();
        LevelAccessor worldIn = event.getWorld();
        // check if block is hay, and if it has been placed on top of an iron block
        // if not, return
        if (!(placedBlock instanceof HayBlock && underBlock.getRegistryName().getPath().equals("iron_block")))
            return;

        BlockPattern.BlockPatternMatch blockPatternMatch = pattern.find(event.getEntity().getLevel(), event.getBlockSnapshot().getPos());

        for(int j = 0; j < pattern.getWidth(); ++j) {
            for(int k = 0; k < pattern.getHeight(); ++k) {
                BlockInWorld blockinworld2 = blockPatternMatch.getBlock(j, k, 0);
                worldIn.setBlock(blockinworld2.getPos(), Blocks.AIR.defaultBlockState(), 2);
                worldIn.levelEvent(2001, blockinworld2.getPos(), Block.getId(blockinworld2.getState()));
            }
        }

        BlockPos blockpos = blockPatternMatch.getBlock(1, 2, 0).getPos();
        HayGolemEntity hayGolem = RegistryHandler.HAY_GOLEM.get().create((Level)worldIn);
        hayGolem.moveTo((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.05D, (double)blockpos.getZ() + 0.5D, 0.0F, 0.0F);
        worldIn.addFreshEntity(hayGolem);

//        for(ServerPlayer serverplayer1 : worldIn.getEntitiesOfClass(ServerPlayer.class, worldIn.getBoundingBox().inflate(5.0D))) {
//            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayer1, irongolem);
//        }

        for(int i1 = 0; i1 < pattern.getWidth(); ++i1) {
            for(int j1 = 0; j1 < pattern.getHeight(); ++j1) {
                BlockInWorld blockinworld1 = blockPatternMatch.getBlock(i1, j1, 0);
                worldIn.blockUpdated(blockinworld1.getPos(), Blocks.AIR);
            }
        }
    }
}
