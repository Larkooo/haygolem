package com.larko.haygolem.Networking;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.larko.haygolem.Main;
import com.larko.haygolem.Graphics.Screens.FarmGui;
import com.larko.haygolem.World.Farm;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class FarmPacket implements IMessage {
    private Farm farm;

    @Override
    public void fromBytes(ByteBuf buf) {
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

    @Override
    public void toBytes(ByteBuf buf) {
    	NBTTagCompound farmTag = farm.serialize();
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
    
    public FarmPacket() {}
    
    public FarmPacket(Farm farm) {
        this.farm = farm;
    }

    public static class Handler implements IMessageHandler<FarmPacket, IMessage> {
        @Override
        public IMessage onMessage(FarmPacket message, MessageContext ctx) {
            // Always use a construct like this to actually handle your message. This ensures that
            // your 'handle' code is run on the main Minecraft thread. 'onMessage' itself
            // is called on the networking thread so it is not safe to do a lot of things
        	// here.
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(FarmPacket message, MessageContext ctx) {
            // code on the client side, show the gui
        	//Minecraft.getMinecraft().displayGuiScreen(new FarmGui(message.farm));
        	Main.commonProxy.displayFarmGui(message.farm);
        }
    }
}
