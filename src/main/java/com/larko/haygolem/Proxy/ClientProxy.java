package com.larko.haygolem.Proxy;

import com.larko.haygolem.Graphics.Screens.FarmGui;
import com.larko.haygolem.World.Farm;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy {
	@Override
	public void showFarmGui(Farm farm, World world, EntityPlayer player)
	{
		if (!world.isRemote)
			displayFarmGui(farm);
	}
	
	@Override
	public void displayFarmGui(Farm farm)
	{
		Minecraft.getMinecraft().displayGuiScreen(new FarmGui(farm));
	}
}
