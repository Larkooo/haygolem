package com.larko.haygolem.Entity;

import com.larko.haygolem.World.Farm;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class HayGolemEntity extends EntityGolem implements net.minecraftforge.common.capabilities.ICapabilitySerializable<NBTTagCompound>/*implements net.minecraftforge.common.capabilities.ICapabilitySerializable<NBTTagCompound>*/ {

    public static final int CAPACITY = 27;
    public static final List<Item> USABLE_TOOLS = Arrays.asList(
            Items.WOODEN_HOE,
            Items.STONE_HOE,
            Items.IRON_HOE,
            Items.GOLDEN_HOE,
            Items.DIAMOND_HOE
    );

    @Nullable
    private Farm farm;
    private InventoryBasic inventory;
    //public ItemStackHandler inventory = new ItemStackHandler(36);

    public HayGolemEntity(World worldIn)
    {
        super(worldIn);
        this.setSize(1.4F, 2.7F);
        this.inventory = new InventoryBasic("HayGolem", false, CAPACITY);
        this.inventory.addInventoryChangeListener(invBasic -> onInventoryChange());
    }


    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
    }


    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        System.out.println(this.getHeldItemMainhand().toString());
    }

    public IInventory getInventory()
    {
        return this.inventory;
    }
    
    public void onInventoryChange()
    {
        for (int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack itemStack = inventory.getStackInSlot(i);
            
            if (USABLE_TOOLS.contains(itemStack.getItem()))
                if (this.getHeldItemMainhand().getItem() != itemStack.getItem())
                    this.setHeldItem(EnumHand.MAIN_HAND, itemStack);
            else
                if (this.getHeldItemMainhand().getItem() == itemStack.getItem())
                    this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.AIR));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList nbttaglist = compound.getTagList("Inventory", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            ItemStack itemstack = new ItemStack(nbttaglist.getCompoundTagAt(i));

            if (!itemstack.isEmpty())
            {
                this.inventory.addItem(itemstack);
            }
        }

        this.setCanPickUpLoot(true);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.inventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = this.inventory.getStackInSlot(i);

            if (!itemstack.isEmpty())
            {
                nbttaglist.appendTag(itemstack.writeToNBT(new NBTTagCompound()));
            }
        }

        compound.setTag("Inventory", nbttaglist);
    }
}
