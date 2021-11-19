package com.larko.haygolem.Entity;

import com.larko.haygolem.Entity.AI.HayGolemHarvestAI;
import com.larko.haygolem.Entity.AI.HayGolemSearchFarmAI;
import com.larko.haygolem.Managers.FarmManager;
import com.larko.haygolem.World.Farm;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class HayGolemEntity extends EntityGolem implements net.minecraftforge.common.capabilities.ICapabilitySerializable<NBTTagCompound> {
    public static final int CAPACITY = 27;
    public static final List<Item> USABLE_TOOLS = Arrays.asList(
            Items.WOODEN_HOE,
            Items.GOLDEN_HOE,
            Items.STONE_HOE,
            Items.IRON_HOE,
            Items.DIAMOND_HOE
    );

    @Nullable
    public Farm farm;

    private InventoryBasic inventory;

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
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new HayGolemSearchFarmAI(this, 1.0f, 100));
        this.tasks.addTask(2, new HayGolemHarvestAI(this,  1.0f));
        this.tasks.addTask(3, new EntityAITempt(this, 1.0f, Item.getItemFromBlock(Blocks.HAY_BLOCK), false));
        // broken
        // this.tasks.addTask(2, new HayGolemWanderAI(this, 0.4f));
        //this.tasks.addTask(2, new EntityAIWander(this, 0.4f));
        this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(5, new EntityAILookIdle(this));
    }

    @Override
    public void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(100.0D);
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

    public boolean isInventoryFull()
    {
        for (int i = 0; i < this.inventory.getSizeInventory(); i++)
        {
            if (this.inventory.getStackInSlot(i).isEmpty())
                return false;
        }
        return true;
    }

    public ItemStack hasToolInInventory()
    {
        ItemStack bestTool = null;

        for (int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if (itemstack.isEmpty() || !USABLE_TOOLS.contains(itemstack.getItem()))
                continue;


            if (bestTool == null)
            {
                bestTool = itemstack;
                continue;
            }

            if (USABLE_TOOLS.indexOf(itemstack.getItem()) > USABLE_TOOLS.indexOf(bestTool.getItem()))
                bestTool = itemstack;
        }

        return bestTool;
    }

    @Nullable
    public ItemStack getItem(Item item)
    {
        for (int i = 0; i < this.inventory.getSizeInventory(); i++)
        {
            ItemStack itemStack = this.inventory.getStackInSlot(i);

            if (!itemStack.isEmpty() && itemStack.getItem() == item)
            {
                return itemStack;
            }
        }

        return null;
    }

    public boolean hasItem(Item item)
    {
        for (int i = 0; i < this.inventory.getSizeInventory(); i++)
        {
            ItemStack itemstack = this.inventory.getStackInSlot(i);

            if (!itemstack.isEmpty() && itemstack.getItem() == item)
            {
                return true;
            }
        }

        return false;
    }

    public boolean hasPlantableItem()
    {
        for (int i = 0; i < this.inventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = this.inventory.getStackInSlot(i);

            if (!itemstack.isEmpty() &&
                    (itemstack.getItem() == Items.WHEAT_SEEDS
                            || itemstack.getItem() == Items.POTATO
                            || itemstack.getItem() == Items.CARROT
                            || itemstack.getItem() == Items.BEETROOT_SEEDS
                    )
            )
            {
                return true;
            }
        }

        return false;
    }

    public void onInventoryChange()
    {
        ItemStack tool = hasToolInInventory();
        if (tool == null)
            return;

        this.setHeldItem(EnumHand.MAIN_HAND, tool);
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (this.farm != null)
            this.farm.workersCount--;
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

        // farm
        try
        {
            this.farm = FarmManager.findByUuid(UUID.fromString(compound.getString("farm")));
            if (this.farm != null)
                this.farm.workersCount++;
        }
        catch (IllegalArgumentException e)
        {
            this.farm = null;
        }

        //this.setCanPickUpLoot(true);
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
        // farm
        if (this.farm != null)
            compound.setString("farm", this.farm.getUuid().toString());
    }
}
