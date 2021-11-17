package com.larko.haygolem.World;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.UUID;

public class Farm {
    UUID uuid;

    private BlockPos startingPos;
    private Vec3i size;

    private int dimensionId;

    UUID ownerUuid;

    public Farm(UUID ownerUuid, BlockPos startingPos, Vec3i size, int dimension)
    {
        this.uuid = UUID.randomUUID();

        this.startingPos = startingPos;
        this.size = size;
        this.dimensionId = dimension;
        this.ownerUuid = ownerUuid;
    }

    public Farm(UUID uuid, UUID ownerUuid, BlockPos startingPos, Vec3i size, int dimension)
    {
        this.uuid = uuid;

        this.startingPos = startingPos;
        this.size = size;
        this.dimensionId = dimension;
        this.ownerUuid = ownerUuid;
    }

    public boolean isWithinBounds(BlockPos pos)
    {
        Vec3i endingPos = startingPos.add(this.size);
        // if size is negative, then the position should be bigger than the ending pos
        return (size.getX() < 0 ?
                pos.getX() <= startingPos.getX() &&
                    pos.getX() >= endingPos.getX()
                : pos.getX() >= startingPos.getX() &&
                    pos.getX() <= endingPos.getX()) &&

                (size.getY() < 0 ?
                pos.getY() <= startingPos.getY() &&
                        pos.getY() >= endingPos.getY() - 1
                : pos.getY() >= startingPos.getY() - 1 &&
                    pos.getY() <= endingPos.getY()) &&

                (size.getZ() < 0 ?
                    pos.getZ() <= startingPos.getZ() &&
                        pos.getZ() >= endingPos.getZ()
                    : pos.getZ() >= startingPos.getZ() &&
                        pos.getZ() <= endingPos.getZ());
    }

    public UUID getUuid()
    {
        return this.uuid;
    }

    public BlockPos getStartingPos()
    {
        return this.startingPos;
    }

    public Vec3i getSize()
    {
        return this.size;
    }

    public BlockPos getCenter()
    {
        Vec3i halfSize = new Vec3i(size.getX() / 2, size.getY() / 2, size.getZ() / 2);
        return startingPos.add(halfSize);
    }

    public NBTTagCompound serialize()
    {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setString("UUID", this.uuid.toString());
        tag.setString("OWNERUUID", this.ownerUuid.toString());

        int[] farmData = {
                this.startingPos.getX(),
                this.startingPos.getY(),
                this.startingPos.getZ(),

                this.size.getX(),
                this.size.getY(),
                this.size.getZ(),

                this.dimensionId
        };
        tag.setIntArray("FARMDATA", farmData);

        return tag;
    }

    public static Farm deserialize(NBTTagCompound tag)
    {
        UUID uuid = UUID.fromString(tag.getString("UUID"));
        // pos (3), size (3), dimension (1)
        int[] farmData = tag.getIntArray("FARMDATA");
        UUID ownerUuid = UUID.fromString(tag.getString("OWNERUUID"));

        return new Farm(
                uuid,
                ownerUuid,
                new BlockPos(farmData[0], farmData[1], farmData[2]),
                new Vec3i(farmData[3], farmData[4], farmData[5]),
                farmData[6]
        );
    }
}
