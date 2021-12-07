package com.larko.haygolem.Managers;

import com.larko.haygolem.Block.FarmMarkerBlock;
import com.larko.haygolem.Serializers.FarmSerializer;
import com.larko.haygolem.Util.Metadata;
import com.larko.haygolem.World.Farm;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid= Metadata.MODID)
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

    public static final Class<? extends Block> boundaryBlock = FarmMarkerBlock.class;

    // create a farm
    @SubscribeEvent
    public static void onPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer))
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

        int dimensionId = 1;
        ResourceLocation dimensionLoc = event.getBlockSnapshot().getLevel().dimensionType().effectsLocation();

        if (dimensionLoc == DimensionType.NETHER_EFFECTS)
            dimensionId = 2;
        else if (dimensionLoc == DimensionType.END_EFFECTS)
            dimensionId = 3;

        farms.add(new Farm(
                event.getEntity().getUUID(),
                startingPoint,
                new Vec3i(sizeX, sizeY, sizeZ),
                dimensionId
                ));
        FarmSerializer.get(event.getWorld().getServer()).setDirty();


    }

    // delete destroyed farm boundaries
    @SubscribeEvent
    public static void onBlockDestroy(BlockEvent.BreakEvent event)
    {
        if (event.getWorld().isClientSide())
            return;

        // check if broke block is boundary block
        if (!(event.getWorld().getBlockState(event.getPos()).getBlock().getClass() == FarmManager.boundaryBlock))
            return;

        // check if is within a farm
        for (int i = 0; i < farms.size(); i++)
        {
            if (farms.get(i).isWithinBounds(event.getPos()))
            {
                // remove the farm
                farms.remove(i);
                return;
            }
        }

        FarmSerializer.get(event.getWorld().getServer()).setDirty();
    }

    public static Farm findByUuid(UUID uuid)
    {
        for (Farm farm : farms)
        {
            if (farm.getUuid().equals(uuid))
                return farm;
        }

        return null;
    }
}
