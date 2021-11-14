package com.larko.haygolem.Graphics.Renderers;

import com.larko.haygolem.Entity.HayGolemEntity;
import com.larko.haygolem.Graphics.Layers.HayGolemHeldItemLayer;
import com.larko.haygolem.Graphics.Models.HayGolemModel;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class HayGolemRenderer extends RenderLiving<HayGolemEntity> {

    private static final ResourceLocation TEXTURES = new ResourceLocation("textures/entity/iron_golem.png");

    public HayGolemRenderer(RenderManager manager)
    {
        // same model as iron golem
        super(manager, new HayGolemModel(), 0.5f);
        // TODO: show held item, make custom layer
        this.addLayer(new HayGolemHeldItemLayer(this));
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(HayGolemEntity entity) {
        return TEXTURES;
    }

    @Override
    protected void applyRotations(HayGolemEntity entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
    }
}
