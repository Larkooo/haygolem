package com.larko.haygolem.Handlers;

import com.larko.haygolem.Networking.FarmPacket;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static int packetId = 0;

    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE = null;

    public PacketHandler() {
    }

    public static int nextID() {
        return packetId++;
    }

    public static void registerMessages(String channelName) {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation("haygolem", channelName),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );
        registerMessages();
    }

    public static void registerMessages() {
        // register messages sent from server to client
        INSTANCE.registerMessage(packetId, FarmPacket.class, FarmPacket::write, FarmPacket::new, FarmPacket::handle);
    }
}