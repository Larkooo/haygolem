package com.larko.haygolem.Entity.AI;

import com.larko.haygolem.Entity.HayGolemEntity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class HayGolemWanderAI extends EntityAIWanderAvoidWater {
    HayGolemEntity hayGolem;

    public HayGolemWanderAI(HayGolemEntity hayGolem, double speed) {
        super(hayGolem, speed);
        this.hayGolem = hayGolem;
    }

    @Override
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
        Vec3d position = super.getPosition();

        if (this.hayGolem.farm != null)
            return this.hayGolem.farm.isWithinBounds(new BlockPos(position)) ? position : this.getPosition();
        return position;
    }
}
