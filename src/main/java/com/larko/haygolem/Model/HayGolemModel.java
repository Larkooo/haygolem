package com.larko.haygolem.Model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HayGolemModel extends ModelBiped
{
    public ModelRenderer hayGolemHead;
    public ModelRenderer hayGolemBody;
    public ModelRenderer hayGolemRightArm;
    public ModelRenderer hayGolemLeftArm;
    public ModelRenderer hayGolemLeftLeg;
    public ModelRenderer hayGolemRightLeg;

    public HayGolemModel()
    {
        this(0.0F);
    }

    public HayGolemModel(float p_i1161_1_)
    {
        this(p_i1161_1_, -7.0F);
    }

    public HayGolemModel(float p_i46362_1_, float p_i46362_2_)
    {
        int i = 128;
        int j = 128;
        this.hayGolemHead = (new ModelRenderer(this)).setTextureSize(128, 128);
        this.hayGolemHead.setRotationPoint(0.0F, 0.0F + p_i46362_2_, -2.0F);
        this.hayGolemHead.setTextureOffset(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8, 10, 8, p_i46362_1_);
        this.hayGolemHead.setTextureOffset(24, 0).addBox(-1.0F, -5.0F, -7.5F, 2, 4, 2, p_i46362_1_);
        this.hayGolemBody = (new ModelRenderer(this)).setTextureSize(128, 128);
        this.hayGolemBody.setRotationPoint(0.0F, 0.0F + p_i46362_2_, 0.0F);
        this.hayGolemBody.setTextureOffset(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18, 12, 11, p_i46362_1_);
        this.hayGolemBody.setTextureOffset(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9, 5, 6, p_i46362_1_ + 0.5F);
        this.hayGolemRightArm = (new ModelRenderer(this)).setTextureSize(128, 128);
        this.hayGolemRightArm.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.hayGolemRightArm.setTextureOffset(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4, 30, 6, p_i46362_1_);
        this.hayGolemLeftArm = (new ModelRenderer(this)).setTextureSize(128, 128);
        this.hayGolemLeftArm.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.hayGolemLeftArm.setTextureOffset(60, 58).addBox(9.0F, -2.5F, -3.0F, 4, 30, 6, p_i46362_1_);
        this.hayGolemLeftLeg = (new ModelRenderer(this, 0, 22)).setTextureSize(128, 128);
        this.hayGolemLeftLeg.setRotationPoint(-4.0F, 18.0F + p_i46362_2_, 0.0F);
        this.hayGolemLeftLeg.setTextureOffset(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, p_i46362_1_);
        this.hayGolemRightLeg = (new ModelRenderer(this, 0, 22)).setTextureSize(128, 128);
        this.hayGolemRightLeg.mirror = true;
        this.hayGolemRightLeg.setTextureOffset(60, 0).setRotationPoint(5.0F, 18.0F + p_i46362_2_, 0.0F);
        this.hayGolemRightLeg.addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, p_i46362_1_);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        //super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.hayGolemHead.render(scale);
        this.hayGolemBody.render(scale);
        this.hayGolemLeftLeg.render(scale);
        this.hayGolemRightLeg.render(scale);
        this.hayGolemRightArm.render(scale);
        this.hayGolemLeftArm.render(scale);
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        //0super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        this.hayGolemHead.rotateAngleY = netHeadYaw * 0.017453292F;
        this.hayGolemHead.rotateAngleX = headPitch * 0.017453292F;
        this.hayGolemLeftLeg.rotateAngleX = -1.5F * this.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.hayGolemRightLeg.rotateAngleX = 1.5F * this.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.hayGolemLeftLeg.rotateAngleY = 0.0F;
        this.hayGolemRightLeg.rotateAngleY = 0.0F;
    }

    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime)
    {
//        EntityhayGolem entityhayGolem = (EntityhayGolem)entitylivingbaseIn;
//        int i = entityhayGolem.getAttackTimer();
//
//        if (i > 0)
//        {
//            this.hayGolemRightArm.rotateAngleX = -2.0F + 1.5F * this.triangleWave((float)i - partialTickTime, 10.0F);
//            this.hayGolemLeftArm.rotateAngleX = -2.0F + 1.5F * this.triangleWave((float)i - partialTickTime, 10.0F);
//        }
//        else
//        {
//            int j = entityhayGolem.getHoldRoseTick();
//
//            if (j > 0)
//            {
//                this.hayGolemRightArm.rotateAngleX = -0.8F + 0.025F * this.triangleWave((float)j, 70.0F);
//                this.hayGolemLeftArm.rotateAngleX = 0.0F;
//            }
//            else
//            {
//                this.hayGolemRightArm.rotateAngleX = (-0.2F + 1.5F * this.triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
//                this.hayGolemLeftArm.rotateAngleX = (-0.2F - 1.5F * this.triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
//            }
//        }
    }

    public ModelRenderer getArmForSide(EnumHandSide side)
    {
        return side == EnumHandSide.LEFT ? this.hayGolemLeftArm : this.hayGolemRightArm;
    }

    public void postRenderArm(float scale, EnumHandSide side)
    {
        ModelRenderer modelrenderer = this.getArmForSide(side);

        modelrenderer.postRender(scale);
    }

    private float triangleWave(float p_78172_1_, float p_78172_2_)
    {
        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5F) - p_78172_2_ * 0.25F) / (p_78172_2_ * 0.25F);
    }
}
