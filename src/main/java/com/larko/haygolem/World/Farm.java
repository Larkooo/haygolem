package com.larko.haygolem.World;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class Farm {
    private World world;
    private BlockPos startingPos;
    private Vec3i size;

    public Farm(BlockPos startingPos, Vec3i size, World world)
    {
        this.startingPos = startingPos;
        this.size = size;
        this.world = world;
    }

    public World getWorld()
    {
        return world;
    }

    public boolean isWithinBounds(BlockPos pos)
    {
        Vec3i endingPos = startingPos.add(size);
        return pos.getX() >= startingPos.getX() &&
                pos.getX() <= endingPos.getX() &&

                pos.getY() >= startingPos.getY() &&
                pos.getY() <= endingPos.getY() &&

                pos.getZ() >= startingPos.getX() &&
                pos.getZ() <= endingPos.getZ();
    }

    public BlockPos getCenter()
    {
        Vec3i halfSize = new Vec3i(size.getX() / 2, size.getY() / 2, size.getZ() / 2);
        return startingPos.add(halfSize);
    }
}
