package com.larko.haygolem.Entity.AI;

import com.larko.haygolem.Entity.HayGolemEntity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class HayGolemWanderAI extends EntityAIBase
{
    protected final HayGolemEntity entity;
    protected double x;
    protected double y;
    protected double z;
    protected final double speed;
    protected int executionChance;
    protected boolean mustUpdate;

    public HayGolemWanderAI(HayGolemEntity creatureIn, double speedIn)
    {
        this(creatureIn, speedIn, 120);
    }

    public HayGolemWanderAI(HayGolemEntity creatureIn, double speedIn, int chance)
    {
        this.entity = creatureIn;
        this.speed = speedIn;
        this.executionChance = chance;
        this.setMutexBits(1);
    }

    public boolean shouldExecute()
    {
        if (!this.mustUpdate)
        {
            if (this.entity.getIdleTime() >= 100)
            {
                return false;
            }

            if (this.entity.getRNG().nextInt(this.executionChance) != 0)
            {
                return false;
            }
        }

        Vec3d vec3d = this.getPosition();

        if (vec3d == null)
        {
            return false;
        }
        else
        {
            this.x = vec3d.x;
            this.y = vec3d.y;
            this.z = vec3d.z;
            this.mustUpdate = false;
            return true;
        }
    }

    @Nullable
    protected Vec3d getPosition()
    {
        Vec3d pos = this.entity.farm != null ?
                RandomPositionGenerator.findRandomTarget(this.entity, Math.abs((this.entity.farm.getSize().getX() + this.entity.farm.getSize().getZ()) / 2) / 2, Math.abs(this.entity.farm.getSize().getY()) / 2)
                : RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);

        if (pos == null)
            return null;

        return this.entity.farm != null ?
                (this.entity.farm.isWithinBounds(new BlockPos(pos)) ? pos : this.getPosition()) : pos;
    }

    public boolean shouldContinueExecuting()
    {
        return !this.entity.getNavigator().noPath();
    }

    public void startExecuting()
    {
        this.entity.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, this.speed);
    }

    public void makeUpdate()
    {
        this.mustUpdate = true;
    }

    public void setExecutionChance(int newchance)
    {
        this.executionChance = newchance;
    }
}
