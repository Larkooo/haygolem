package com.larko.haygolem.Networking;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.function.Supplier;

import com.larko.haygolem.Main;
import com.larko.haygolem.Graphics.Screens.FarmGui;
import com.larko.haygolem.World.Farm;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class FarmPacket {
    private Farm farm;

    public FarmPacket(Farm farm) {
        this.farm = farm;
    }

    public FarmPacket(FriendlyByteBuf buf)
    {
        int uuidSize = 4*9;

        byte[] uuid = new byte[uuidSize];
        byte[] ownerUuid = new byte[uuidSize];

        buf.readBytes(uuid);
        buf.readBytes(ownerUuid);

        this.farm = new Farm(
                // farm uuid
                UUID.fromString(new String(uuid)),
                // owner uuid
                UUID.fromString(new String(ownerUuid)),
                // start pos
                new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()),
                // size
                new Vec3i(buf.readInt(), buf.readInt(), buf.readInt()),
                // dimension id
                buf.readInt()
        );

        this.farm.workersCount = buf.readInt();
    }

    public void write(FriendlyByteBuf buf) {
        CompoundTag farmTag = farm.serialize();
        int[] farmData = farmTag.getIntArray("FARMDATA");
        String uuid = farmTag.getString("UUID");
        String ownerUuid = farmTag.getString("OWNERUUID");

        // uuid
        buf.writeBytes(uuid.getBytes());
        // owner uuid
        buf.writeBytes(ownerUuid.getBytes());

        // farm data
        for (int d : farmData)
            buf.writeInt(d);

        // number of workers
        buf.writeInt(this.farm.workersCount);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Main.proxy.displayFarmGui(this.farm);
        });
        ctx.get().setPacketHandled(true);
    }

//    public static class Handler {
//        public static void onMessage(FarmPacket msg, Supplier<NetworkEvent.Context> ctx) {
//            // Always use a construct like this to actually handle your message. This ensures that
//            // your 'handle' code is run on the main Minecraft thread. 'onMessage' itself
//            // is called on the networking thread so it is not safe to do a lot of things
//        	// here.
//            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
//            return null;
//        }
//
//        private void handle(FarmPacket message, MessageContext ctx) {
//            // code on the client side, show the gui
//        	//Minecraft.getMinecraft().displayGuiScreen(new FarmGui(message.farm));
//        	Main.commonProxy.displayFarmGui(message.farm);
//        }
//    }
}
