package com.larko.haygolem.Entity;

import com.larko.haygolem.Entity.AI.HayGolemHarvestAI;
import com.larko.haygolem.Entity.AI.HayGolemSearchFarmAI;
import com.larko.haygolem.Managers.FarmManager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class HayGolemEntity extends AbstractGolem {
    public static final int CAPACITY = 27;
    public static final List<Item> USABLE_TOOLS = Arrays.asList(
            Items.WOODEN_HOE,
            Items.GOLDEN_HOE,
            Items.STONE_HOE,
            Items.IRON_HOE,
            Items.DIAMOND_HOE
    );
    public static List<Item> PLANTABLE_ITEMS = Arrays.asList(
            Items.WHEAT_SEEDS,
            Items.POTATO,
            Items.CARROT,
            Items.BEETROOT_SEEDS
    );

    // API
    public static void addPlantableItem(Item item)
    {
        PLANTABLE_ITEMS.add(item);
    }
    public static void removePlantableItem(Item item)
    {
        PLANTABLE_ITEMS.remove(item);
    }


    // farm uuid
    @Nullable
    public UUID farm;

    private SimpleContainer inventory;

    public HayGolemEntity(EntityType<? extends HayGolemEntity> type, Level worldIn)
    {
        super(type, worldIn);
        //this.inventory = new Inventory("HayGolem", false, CAPACITY);
        this.inventory = new SimpleContainer(CAPACITY);
        this.inventory.addListener(p_18983_ -> onInventoryChange());

//        class listener extends ContainerListener
//        {
//
//            @Override
//            public void containerChanged(Container p_18983_) {
//                p_18983_.
//            }
//        }
//
//        this.inventory.addListener();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.FOLLOW_RANGE, 200);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        //this.tasks.addTask(1, new HayGolemHarvestAI(this, 0.4f));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new HayGolemSearchFarmAI(this, 1.0f, 100));
        this.goalSelector.addGoal(2, new HayGolemHarvestAI(this,  1.0f));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0f, Ingredient.of(Items.HAY_BLOCK), false));
        //this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        // broken
        // this.tasks.addTask(2, new HayGolemWanderAI(this, 0.4f));
        //this.tasks.addTask(2, new EntityAIWander(this, 0.4f));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

//    @Override
//    public void applyEntityAttributes()
//    {
//        super.entity();
//        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
//        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
//        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
//        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(100.0D);
//    }

//    @Override
//    public void onLivingUpdate() {
//        super.update();
////        if (this.farm != null)
////            System.out.println(this.farm.isWithinBounds(this.getPosition()));
//        // search for a farm
////        if (this.status == Status.SEARCHING_FARM)
////        {
////            for (Farm farm : FarmManager.farms)
////            {
////                if (this.getPosition().distanceSq(farm.getCenter()) < 40)
////                {
////                    this.farm = farm;
////                    this.status = Status.IDLING;
////                }
////            }
////        }
//
//
//        //System.out.println(this.getHeldItemMainhand().toString());
//    }

    public SimpleContainer getInventory()
    {
        return this.inventory;
    }

    public boolean isInventoryFull()
    {
        for (int i = 0; i < this.inventory.getContainerSize(); i++)
        {
            if (this.inventory.getItem(i).isEmpty())
                return false;
        }
        return true;
    }

    public ItemStack hasToolInInventory()
    {
        ItemStack bestTool = null;

        for (int i = 0; i < inventory.getContainerSize(); i++)
        {
            ItemStack itemstack = this.inventory.getItem(i);
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

    public boolean hasItem(Item item)
    {
        for (int i = 0; i < this.inventory.getContainerSize(); i++)
        {
            ItemStack itemstack = this.inventory.getItem(i);

            if (!itemstack.isEmpty() && itemstack.getItem() == item)
            {
                return true;
            }
        }

        return false;
    }

    public boolean hasPlantableItem()
    {
        for (int i = 0; i < this.inventory.getContainerSize(); ++i)
        {
            ItemStack itemstack = this.inventory.getItem(i);

            if (!itemstack.isEmpty() && PLANTABLE_ITEMS.contains(itemstack.getItem()))
                return true;
        }

        return false;
    }

    public void onInventoryChange()
    {
        ItemStack tool = hasToolInInventory();
        if (tool == null)
            return;

        this.setItemInHand(InteractionHand.MAIN_HAND, tool);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource p_21385_, int p_21386_, boolean p_21387_) {
        super.dropCustomDeathLoot(p_21385_, p_21386_, p_21387_);

        for (int i = 0; i < this.inventory.getContainerSize(); i++)
        {
            ItemStack itemStack = this.inventory.getItem(i);
            if (itemStack.isEmpty())
                continue;

            this.spawnAtLocation(itemStack);
        }

        if (FarmManager.findByUuid(farm) != null)
        {
            FarmManager.findByUuid(farm).golemsCount--;
        }
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        if (p_21016_ == DamageSource.CACTUS && this.hasItem(Items.SLIME_BALL))
            return false;

        return super.hurt(p_21016_, p_21017_);
    }

    //    @Override
//    public void onDeath(DamageSource cause) {
//        super.onDeath(cause);
//
//        for (int i = 0; i < this.inventory.getSizeInventory(); i++)
//        {
//            ItemStack itemStack = this.inventory.getStackInSlot(i);
//            if (itemStack.isEmpty())
//                continue;
//
//            this.dropItem(itemStack.getItem(), itemStack.getCount());
//        }
//
//        if (this.farm != null)
//            this.farm.workersCount--;
//    }

    public void addAdditionalSaveData(CompoundTag data) {
        super.addAdditionalSaveData(data);

        data.put("Inventory", this.inventory.createTag());
        // farm
        if (this.farm != null)
            data.putString("farm", farm.toString());
    }

    public void readAdditionalSaveData(CompoundTag data) {
        super.readAdditionalSaveData(data);
        ListTag tagList = data.getList("Inventory", 10);

        this.inventory.fromTag(tagList);

        try
        {
            this.farm = UUID.fromString(data.getString("farm"));
            if (FarmManager.findByUuid(this.farm) != null)
            {
                FarmManager.findByUuid(this.farm).golemsCount--;
            }
        }
        catch (IllegalArgumentException e)
        {
            this.farm = null;
        }
    }
}
