package com.larko.haygolem.Proxy;

import com.larko.haygolem.Graphics.Screens.FarmGui;
import com.larko.haygolem.World.Farm;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ClientProxy extends CommonProxy {
	public ClientProxy() {}

	@Override
	public void showFarmGui(Farm farm, Level world, Player player)
	{
		if (world.isClientSide())
			displayFarmGui(farm);
	}
	
	@Override
	public void displayFarmGui(Farm farm)
	{
		Minecraft.getInstance().setScreen(new FarmGui(farm));
		//Minecraft.getInstance().gui..displayGuiScreen(new FarmGui(farm));
	}
}
