package com.larko.haygolem.Proxy;

import com.larko.haygolem.Handlers.PacketHandler;
import com.larko.haygolem.Networking.FarmPacket;
import com.larko.haygolem.World.Farm;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

public class ServerProxy extends CommonProxy {
	@Override
	public void showFarmGui(Farm farm, Level world, Player player)
	{
		PacketHandler.INSTANCE.sendTo(new FarmPacket(farm), ((ServerPlayer) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
	}
}
