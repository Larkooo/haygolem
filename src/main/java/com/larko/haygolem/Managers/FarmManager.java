package com.larko.haygolem.Managers;

import com.larko.haygolem.World.Farm;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHay;
import net.minecraft.block.BlockTorch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class FarmManager {
    // A        B
    //
    // C        D
    //private static ArrayList<BlockPos> currentFarm = new ArrayList<>();
    private static BlockPos startingPoint;
    private static int sizeX;
    private static int sizeY;
    private static int sizeZ;

    private static int pointCounter = 0;

    public static ArrayList<Farm> farms = new ArrayList<>();

    private static Class<? extends Block> boundaryBlock = BlockTorch.class;

    @SubscribeEvent
    public static void onPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof EntityPlayerMP))
            return;

        Block placedBlock = event.getPlacedBlock().getBlock();
        if (!(placedBlock.getClass() == boundaryBlock))
            return;

        pointCounter++;

        switch (pointCounter)
        {
            case 1:
                startingPoint = event.getPos();
                break;
            case 2:
                sizeX = event.getPos().getX() - startingPoint.getX();
                sizeY = event.getPos().getY() - startingPoint.getY();
                sizeZ = event.getPos().getZ() - startingPoint.getZ();
                break;
        }

        if (pointCounter != 2)
            return;

        pointCounter = 0;
        farms.add(new Farm(startingPoint, new Vec3i(sizeX, sizeY, sizeZ), event.getWorld()));
    }
}
