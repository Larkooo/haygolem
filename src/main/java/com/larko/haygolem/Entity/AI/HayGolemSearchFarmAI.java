package com.larko.haygolem.Entity.AI;

import com.larko.haygolem.Entity.HayGolemEntity;
import com.larko.haygolem.Managers.FarmManager;
import com.larko.haygolem.World.Farm;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

public class HayGolemSearchFarmAI extends Goal
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
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse()
    {
        if (this.runDelay > 0)
        {
            --this.runDelay;
            return false;
        }
        else
        {
            this.runDelay = 200 + this.hayGolem.getRandom().nextInt(200);
            if (this.hayGolem.farm != null && !this.hayGolem.farm.isWithinBounds(this.hayGolem.blockPosition()))
                return true;
            else if (this.hayGolem.farm == null && this.searchForDestination())
                return true;
            else
                return false;
        }
    }

    @Override
    public boolean canContinueToUse()
    {
        return this.timeoutCounter >= -this.maxStayTicks && this.timeoutCounter <= 1200 && this.hayGolem.farm != null && !this.hayGolem.farm.isWithinBounds(this.hayGolem.blockPosition());
    }

    @Override
    public void start()
    {
        BlockPos dest = this.hayGolem.farm.getCenter();
        this.hayGolem.getNavigation().moveTo(
                (double)((float)dest.getX()) + 0.5D,
            (double)((float)dest.getY() + 1),
            (double)((float)dest.getZ()) + 0.5D, this.movementSpeed);

        this.timeoutCounter = 0;
        this.maxStayTicks = this.hayGolem.getRandom().nextInt(this.hayGolem.getRandom().nextInt(1200) + 1200) + 1200;
    }

    @Override
    public void tick()
    {
        if (!this.hayGolem.farm.isWithinBounds(this.hayGolem.blockPosition()))
        {
            ++this.timeoutCounter;

            if (this.timeoutCounter % 5 == 0)
            {
                BlockPos dest = this.hayGolem.farm.getCenter();
                this.hayGolem.getNavigation().moveTo((double)((float)dest.getX()) + 0.5D, (double)(dest.getY() + 1), (double)((float)dest.getZ()) + 0.5D, this.movementSpeed);
            }
        }
        else
        {
            --this.timeoutCounter;
            this.hayGolem.getNavigation().stop();
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
            double dist = Math.sqrt(this.hayGolem.distanceToSqr(
                    farm.getCenter().getX(),
                    farm.getCenter().getY(),
                    farm.getCenter().getZ()));

            if (Math.sqrt(this.hayGolem.distanceToSqr(
                    closest.getCenter().getX(),
                    closest.getCenter().getY(),
                    closest.getCenter().getZ())) > dist)
                closest = FarmManager.farms.get(i);
        }

        if (Math.sqrt(this.hayGolem.distanceToSqr(
                closest.getCenter().getX(),
                closest.getCenter().getY(),
                closest.getCenter().getZ())) < this.searchLength)
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
