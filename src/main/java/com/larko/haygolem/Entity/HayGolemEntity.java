package com.larko.haygolem.Entity;

import com.larko.haygolem.World.Farm;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class HayGolemEntity extends EntityIronGolem {
    @Nullable
    Farm farm;

    public HayGolemEntity(World worldIn)
    {
        super(worldIn);
        this.setSize(1.4F, 2.7F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

}
