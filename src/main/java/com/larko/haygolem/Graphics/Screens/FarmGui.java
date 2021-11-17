package com.larko.haygolem.Graphics.Screens;

import com.larko.haygolem.Entity.HayGolemEntity;
import com.larko.haygolem.World.Farm;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;

@SideOnly(Side.CLIENT)
public class FarmGui extends GuiScreen
{
    Farm farm;

    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation DEMO_BACKGROUND_LOCATION = new ResourceLocation("textures/gui/demo_background.png");

    public FarmGui(Farm farm)
    {
        this.farm = farm;
    }

    public void initGui()
    {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1, this.width / 2 - 116, this.height / 2 + 62 + -16, 228, 20, "Done"));
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        switch (button.id)
        {
            case 1:
                this.mc.displayGuiScreen((GuiScreen)null);
                this.mc.setIngameFocus();
        }
    }

    public void drawDefaultBackground()
    {
        super.drawDefaultBackground();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(DEMO_BACKGROUND_LOCATION);
        int i = (this.width - 248) / 2;
        int j = (this.height - 166) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, 248, 166);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        int i = (this.width - 248) / 2 + 10;
        int j = (this.height - 166) / 2 + 8;
        this.fontRenderer.drawString(this.farm.getPlayer() != null ? this.farm.getPlayer().getDisplayNameString() + "'s farm" : "Farm", i, j, 2039583);
        j = j + 12;
        GameSettings gamesettings = this.mc.gameSettings;
        if (this.farm.getPlayer() != null)
            this.fontRenderer.drawString("Owner : " + this.farm.getPlayer().getDisplayNameString(), i, j, 5197647);
        this.fontRenderer.drawString("Assigned hay golems : " + this.farm.workersCount, i, j + 12, 5197647);
        for (int n = 0; n < this.farm.workersCount; n++)
        {
            GuiInventory.drawEntityOnScreen((n * 30) + i + 20, j + 100, 17, (float)(i + 51) - mouseX, (float)(j + 75 - 50) - mouseY, new HayGolemEntity(mc.world));
        }

        BlockPos startingPos = this.farm.getStartingPos();
        Vec3i farmSize = this.farm.getSize();
        BlockPos endingPos = startingPos.add(this.farm.getSize());

        int chests = 0;
        int crops = 0;

        for (int x = startingPos.getX(); farmSize.getX() < 0 ? x >= endingPos.getX() : x <= endingPos.getX(); x += farmSize.getX() < 0 ? -1 : 1)
        {
            for (int y = startingPos.getY() - 1; farmSize.getY() < 0 ? y >= endingPos.getY() : y <= endingPos.getY(); y += farmSize.getY() < 0 ? -1 : 1)
            {
                for (int z = startingPos.getZ(); farmSize.getZ() < 0 ? z >= endingPos.getZ() : z <= endingPos.getZ(); z += farmSize.getZ() < 0 ? -1 : 1)
                {
                    BlockPos pos = new BlockPos(x, y, z);

                    if (mc.world.getBlockState(pos).getBlock() instanceof BlockChest)
                        chests++;
                    else if (mc.world.getBlockState(pos).getBlock() instanceof BlockCrops)
                        crops++;
                }
            }
        }

        this.fontRenderer.drawString("Chests : " + chests, i, j + 24, 5197647);
        this.fontRenderer.drawString("Crops : " + crops, i, j + 36, 5197647);
        //this.fontRenderer.drawSplitString(I18n.format("demo.help.fullWrapped"), i, j + 68, 218, 2039583);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
