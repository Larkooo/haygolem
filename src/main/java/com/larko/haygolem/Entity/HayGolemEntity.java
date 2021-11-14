package com.larko.haygolem.Entity;

import com.larko.haygolem.Managers.FarmManager;
import com.larko.haygolem.World.Farm;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarrot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.init.Blocks;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class HayGolemEntity extends EntityGolem implements net.minecraftforge.common.capabilities.ICapabilitySerializable<NBTTagCompound>/*implements net.minecraftforge.common.capabilities.ICapabilitySerializable<NBTTagCompound>*/ {

//    static class AIMoveFarm extends EntityAIMoveToBlock
//    {
//        private final HayGolemEntity hayGolem;
//
//        public AIMoveFarm(HayGolemEntity hayGolem)
//        {
//            super(hayGolem, 0.699999988079071D, 50);
//            this.hayGolem = hayGolem;
//        }
//
//        public boolean shouldExecute()
//        {
//            if (this.runDelay <= 0)
//            {
//                if (hayGolem.farm == null)
//                    return false;
//            }
//
//            return super.shouldExecute();
//        }
//
//        public boolean shouldContinueExecuting()
//        {
//            return super.shouldContinueExecuting();
//        }
//
//        public void updateTask()
//        {
//            super.updateTask();
//        }
//
//        protected boolean shouldMoveTo(World worldIn, BlockPos pos)
//        {
//            Block block = worldIn.getBlockState(pos).getBlock();
//
//            if (pos == hayGolem.farm.getCenter())
//            {
//                return true;
//            }
//
//            return false;
//        }
//    }

    public enum Status
    {
        SEARCHING_FARM,
        IDLING
    }

    public static final int CAPACITY = 27;
    public static final List<Item> USABLE_TOOLS = Arrays.asList(
            Items.WOODEN_HOE,
            Items.STONE_HOE,
            Items.IRON_HOE,
            Items.GOLDEN_HOE,
            Items.DIAMOND_HOE
    );

    private Status status = Status.SEARCHING_FARM;

    @Nullable
    public Farm farm;

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
        //this.tasks.addTask(1, new HayGolemHarvestAI(this, 0.4f));
        this.tasks.addTask(0, new HayGolemSearchFarmAI(this, 0.4f, 200));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
//        if (this.farm != null)
//            System.out.println(this.farm.isWithinBounds(this.getPosition()));
        // search for a farm
//        if (this.status == Status.SEARCHING_FARM)
//        {
//            for (Farm farm : FarmManager.farms)
//            {
//                if (this.getPosition().distanceSq(farm.getCenter()) < 40)
//                {
//                    this.farm = farm;
//                    this.status = Status.IDLING;
//                }
//            }
//        }


        //System.out.println(this.getHeldItemMainhand().toString());
    }

    public InventoryBasic getInventory()
    {
        return this.inventory;
    }

    public boolean isFarmItemInInventory()
    {
        for (int i = 0; i < this.inventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = this.inventory.getStackInSlot(i);

            if (!itemstack.isEmpty() && (itemstack.getItem() == Items.WHEAT_SEEDS || itemstack.getItem() == Items.POTATO || itemstack.getItem() == Items.CARROT || itemstack.getItem() == Items.BEETROOT_SEEDS))
            {
                return true;
            }
        }

        return false;
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
