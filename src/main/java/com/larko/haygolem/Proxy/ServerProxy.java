package com.larko.haygolem.Proxy;

import com.larko.haygolem.Handlers.PacketHandler;
import com.larko.haygolem.Networking.FarmPacket;
import com.larko.haygolem.World.Farm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class ServerProxy extends CommonProxy {
	@Override
	public void showFarmGui(Farm farm, World world, EntityPlayer player)
	{
		PacketHandler.INSTANCE.sendTo(new FarmPacket(farm), (EntityPlayerMP) player);
	}
}
