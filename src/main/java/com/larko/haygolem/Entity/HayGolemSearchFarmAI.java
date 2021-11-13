package com.larko.haygolem.Entity;

import com.larko.haygolem.Managers.FarmManager;
import com.larko.haygolem.World.Farm;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HayGolemSearchFarmAI extends EntityAIMoveToBlock {
    private final HayGolemEntity hayGolem;
    private final int searchLength;

    public HayGolemSearchFarmAI(HayGolemEntity hayGolem, double speedIn, int length) {
        super(hayGolem, speedIn, length);
        this.searchLength = length;
        this.hayGolem = hayGolem;
    }

    @Override
    public boolean shouldExecute() {
        if (this.runDelay <= 0)
        {
            if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.hayGolem.world, this.hayGolem))
                return false;
        }

        return super.shouldExecute();
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (hayGolem.farm != null)
            return false;

        return super.shouldContinueExecuting();
    }

    @Override
    public void updateTask() {
        super.updateTask();

    }

    @Override
    protected boolean shouldMoveTo(World worldIn, BlockPos pos) {
        for (Farm farm : FarmManager.farms)
        {
            System.out.println(hayGolem.getPosition().distanceSq(farm.getCenter()));
//            if (hayGolem.getPosition().distanceSq(farm.getCenter()) > this.searchLength)
//                continue;

            if (farm.isWithinBounds(pos))
                return true;
        }
        return false;
    }
}
