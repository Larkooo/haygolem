package com.larko.haygolem.Entity.AI;

import com.larko.haygolem.Entity.HayGolemEntity;
import com.larko.haygolem.Managers.FarmManager;
import com.larko.haygolem.World.Farm;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;

public class HayGolemSearchFarmAI extends EntityAIBase
{
    private final HayGolemEntity hayGolem;
    private final double movementSpeed;
    protected int runDelay;
    private int timeoutCounter;
    private int maxStayTicks;
    private final int searchLength;

    public HayGolemSearchFarmAI(HayGolemEntity hayGolem, double speedIn, int length)
    {
        this.hayGolem = hayGolem;
        this.movementSpeed = speedIn;
        this.searchLength = length;
        this.setMutexBits(5);
    }

    public boolean shouldExecute()
    {
        if (this.runDelay > 0)
        {
            --this.runDelay;
            return false;
        }
        else
        {
            this.runDelay = 200 + this.hayGolem.getRNG().nextInt(200);
            return (this.hayGolem.farm != null && FarmManager.farms.contains(this.hayGolem.farm) && !this.hayGolem.farm.isWithinBounds(this.hayGolem.getPosition())) || this.searchForDestination();
        }
    }

    public boolean shouldContinueExecuting()
    {
        return this.timeoutCounter >= -this.maxStayTicks && this.timeoutCounter <= 1200 && this.hayGolem.farm != null && FarmManager.farms.contains(this.hayGolem.farm) && !this.hayGolem.farm.isWithinBounds(this.hayGolem.getPosition());
    }

    public void startExecuting()
    {
        BlockPos dest = this.hayGolem.farm.getCenter();
        this.hayGolem.getNavigator().tryMoveToXYZ(
                (double)((float)dest.getX()) + 0.5D,
            (double)((float)dest.getY() + 1),
            (double)((float)dest.getZ()) + 0.5D, this.movementSpeed);

        this.timeoutCounter = 0;
        this.maxStayTicks = this.hayGolem.getRNG().nextInt(this.hayGolem.getRNG().nextInt(1200) + 1200) + 1200;
    }

    public void updateTask()
    {
        if (!this.hayGolem.farm.isWithinBounds(new BlockPos(this.hayGolem)))
        {
            ++this.timeoutCounter;

            if (this.timeoutCounter % 5 == 0)
            {
                BlockPos dest = this.hayGolem.farm.getCenter();
                this.hayGolem.getNavigator().tryMoveToXYZ((double)((float)dest.getX()) + 0.5D, (double)(dest.getY() + 1), (double)((float)dest.getZ()) + 0.5D, this.movementSpeed);
            }
        }
        else
        {
            --this.timeoutCounter;
            this.hayGolem.getNavigator().clearPath();
        }
    }


    private boolean searchForDestination()
    {
        if (FarmManager.farms.size() == 0)
            return false;

        Farm closest = FarmManager.farms.get(0);

        for (int i = 1; i < FarmManager.farms.size(); i++)
        {
            Farm farm = FarmManager.farms.get(i);
            double dist = this.hayGolem.getDistance(
                    farm.getCenter().getX(),
                    farm.getCenter().getY(),
                    farm.getCenter().getZ());

            if (this.hayGolem.getDistance(
                    closest.getCenter().getX(),
                    closest.getCenter().getY(),
                    closest.getCenter().getZ()) > dist)
                closest = FarmManager.farms.get(i);
        }

        if (this.hayGolem.getDistance(
                closest.getCenter().getX(),
                closest.getCenter().getY(),
                closest.getCenter().getZ()) < this.searchLength)
        {
            this.hayGolem.farm = closest;
            this.hayGolem.farm.workersCount++;
            return true;
        }

        return false;
    }

//    private boolean shouldMoveTo(World worldIn, BlockPos pos)
//    {
//        for (Farm farm : FarmManager.farms)
//        {
//            if (farm.isWithinBounds(pos))
//            {
//                this.hayGolem.farm = farm;
//                return true;
//            }
//        }
//        return false;
//    }
}
