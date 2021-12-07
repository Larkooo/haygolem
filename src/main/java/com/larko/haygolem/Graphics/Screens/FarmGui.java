package com.larko.haygolem.Graphics.Screens;

import com.larko.haygolem.Entity.HayGolemEntity;
import com.larko.haygolem.Handlers.RegistryHandler;
import com.larko.haygolem.World.Farm;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;

@OnlyIn(Dist.CLIENT)
public class FarmGui extends Screen
{
    Farm farm;

    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation DEMO_BACKGROUND_LOCATION = new ResourceLocation("textures/gui/demo_background.png");

    public FarmGui(Farm farm)
    {
        super(new TextComponent("Farm"));
        this.farm = farm;
    }

    public void init()
    {
        int i = -16;
        this.addRenderableWidget(new Button(this.width / 2 - 116, this.height / 2 + 62 + -16, 228, 20, new TextComponent("Done"), (p_95948_) -> {
            this.minecraft.setScreen((Screen)null);
            this.minecraft.mouseHandler.grabMouse();
        }));
    }

    public void renderBackground(PoseStack p_95941_)
    {
        super.renderBackground(p_95941_);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, DEMO_BACKGROUND_LOCATION);
        int i = (this.width - 248) / 2;
        int j = (this.height - 166) / 2;
        this.blit(p_95941_, i, j, 0, 0, 248, 166);
    }

    public void render(PoseStack p_95943_, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(p_95943_);

        Player farmOwner = this.minecraft.level.getPlayerByUUID(this.farm.getOwnerUuid());

        int i = (this.width - 248) / 2 + 10;
        int j = (this.height - 166) / 2 + 8;
        MultiLineLabel.create(this.font, new TextComponent(farmOwner != null ? farmOwner.getDisplayName().getString() + "'s farm" : "Farm"))
                .renderLeftAlignedNoShadow(p_95943_, i, j, 9, 5197647);
        j = j + 12;

        if (farmOwner != null)
            MultiLineLabel.create(this.font, new TextComponent("Owner : ").append(farmOwner.getDisplayName()))
                    .renderLeftAlignedNoShadow(p_95943_, i, j, 9, 5197647);

        MultiLineLabel.create(this.font, new TextComponent("Assigned hay golems : " + this.farm.workersCount))
                .renderLeftAlignedNoShadow(p_95943_, i, j+12, 9, 5197647);
        for (int n = 0; n < this.farm.workersCount; n++)
        {
            InventoryScreen.renderEntityInInventory((n * 30) + i + 20, j + 100, 17, (float)(i + 51) - mouseX, (float)(j + 75 - 50) - mouseY, RegistryHandler.HAY_GOLEM.get().create(this.minecraft.level));
        }

        BlockPos startingPos = this.farm.getStartingPos();
        Vec3i farmSize = this.farm.getSize();
        BlockPos endingPos = startingPos.offset(this.farm.getSize());

        int chests = 0;
        int crops = 0;

        for (int x = startingPos.getX(); farmSize.getX() < 0 ? x >= endingPos.getX() : x <= endingPos.getX(); x += farmSize.getX() < 0 ? -1 : 1)
        {
            for (int y = startingPos.getY() - 1; farmSize.getY() < 0 ? y >= endingPos.getY() : y <= endingPos.getY(); y += farmSize.getY() < 0 ? -1 : 1)
            {
                for (int z = startingPos.getZ(); farmSize.getZ() < 0 ? z >= endingPos.getZ() : z <= endingPos.getZ(); z += farmSize.getZ() < 0 ? -1 : 1)
                {
                    BlockPos pos = new BlockPos(x, y, z);

                    if (this.minecraft.level.getBlockState(pos).getBlock() instanceof ChestBlock)
                        chests++;
                    else if (this.minecraft.level.getBlockState(pos).getBlock() instanceof CropBlock)
                        crops++;
                }
            }
        }

        MultiLineLabel.create(this.font, new TextComponent("Chests : " + chests)).renderLeftAlignedNoShadow(p_95943_, i, j+24, 12, 5197647);
        MultiLineLabel.create(this.font, new TextComponent("Crops : " + crops)).renderLeftAlignedNoShadow(p_95943_, i, j+36, 12, 5197647);
        super.render(p_95943_, mouseX, mouseY, partialTicks);
    }
}
