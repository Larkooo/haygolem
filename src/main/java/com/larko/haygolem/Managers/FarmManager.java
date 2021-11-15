package com.larko.haygolem.Managers;

import com.larko.haygolem.Serializers.FarmSerializer;
import com.larko.haygolem.Util.Metadata;
import com.larko.haygolem.World.Farm;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHay;
import net.minecraft.block.BlockTorch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

@Mod.EventBusSubscriber(modid= Metadata.MODID)public class FarmManager {
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

    // create a farm
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
        farms.add(new Farm(event.getEntity().getUniqueID(), startingPoint, new Vec3i(sizeX, sizeY, sizeZ), event.getBlockSnapshot().getDimId()));
    }

    // delete destroyed farm boundaries
//    @SubscribeEvent
//    public static void onBlockDestroy()

    public static Farm findByUuid(UUID uuid)
    {
        for (Farm farm : farms)
        {
            if (farm.getUuid().equals(uuid))
                return farm;
        }

        return null;
    }

    public static void serialize()
    {
        FarmSerializer store = FarmSerializer.get();

        // remove all data
        if(store != null && store.data != null && store.data.getSize() > 0) {
            Set<String> toRemove = new HashSet<String>();
            for(String key : store.data.getKeySet()) { // Remove all data
                if(!key.equals("")) {
                    toRemove.add(key);
                }
            }
            for(String key : toRemove) {
                store.data.removeTag(key);
            }
        }

        // patch new data
        for (Farm farm : farms)
        {
            NBTTagCompound data = farm.serialize();

            store.data.setTag("FARM_" + farm.getUuid().toString(), data);
            store.markDirty();
        }
    }

    public static void deserialize()
    {
        farms.clear();

        FarmSerializer store = FarmSerializer.get();
        if (store.data != null)
        {
            for (String key : store.data.getKeySet())
            {
                Farm farm = Farm.deserialize(store.data.getCompoundTag(key));

                farms.add(farm);
            }
        }
    }
}
