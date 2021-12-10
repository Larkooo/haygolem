package com.larko.haygolem.Entity.AI;

import com.larko.haygolem.Entity.HayGolemEntity;
import com.larko.haygolem.Managers.FarmManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.EnumSet;
import java.util.List;

public class HayGolemHarvestAI extends Goal
{
    private final HayGolemEntity hayGolem;
    private final double movementSpeed;
    protected int runDelay;
    private int timeoutCounter;
    private int maxStayTicks;
    protected BlockPos destinationBlock = BlockPos.ZERO;
    private boolean closeToDestination;

    enum Task
    {
        IDLING,
        PLANT_CROPS,
        HARVEST_CROPS,
        HARVEST_SPECIFIC_BLOCKS,
        PLANT_REEDS,
        HARVEST_REEDS,
        PLANT_CACTUS,
        HARVEST_CACTUS,
        USE_BONEMEAL,
        DEPOSIT_CHEST
    }

    private Task currentTask = Task.IDLING;

    public HayGolemHarvestAI(HayGolemEntity hayGolem, double speedIn)
    {
        this.hayGolem = hayGolem;
        this.movementSpeed = speedIn;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse()
    {
        if (this.runDelay > 0)
        {
            --this.runDelay;
            return false;
        }
        else
        {
            this.runDelay = 10 + this.hayGolem.getRandom().nextInt(10);
            return FarmManager.findByUuid(this.hayGolem.farm) != null && this.searchForDestination();
        }
    }

    @Override
    public boolean canContinueToUse()
    {
        //if (this.currentTask == Task.IDLING && this.hayGolem.farm.focusedBlocks.contains(this.destinationBlock))
        //    this.hayGolem.farm.focusedBlocks.remove(this.destinationBlock);

        return FarmManager.findByUuid(this.hayGolem.farm) != null && this.currentTask != Task.IDLING && this.timeoutCounter >= -this.maxStayTicks && this.timeoutCounter <= 1200 && this.shouldMoveTo(this.hayGolem.level, this.destinationBlock);
    }

    @Override
    public void start()
    {
        this.hayGolem.getNavigation().moveTo((double)((float)this.destinationBlock.getX()) + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)((float)this.destinationBlock.getZ()) + 0.5D, this.movementSpeed);
        this.timeoutCounter = 0;
        this.maxStayTicks = this.hayGolem.getRandom().nextInt(this.hayGolem.getRandom().nextInt(1200) + 1200) + 1200;
    }

    @Override
    public void tick()
    {
        if (Math.sqrt(this.hayGolem.distanceToSqr(new Vec3(this.destinationBlock.above().getX(), this.destinationBlock.above().getY(), this.destinationBlock.above().getZ()))) > 3.0 || (this.currentTask == Task.HARVEST_CACTUS && Math.sqrt(this.hayGolem.distanceToSqr(new Vec3(this.destinationBlock.above().getX(), this.destinationBlock.above().getY(), this.destinationBlock.above().getZ()))) > 6.0))
        {
            this.closeToDestination = false;
            ++this.timeoutCounter;

            if (this.timeoutCounter % 40 == 0)
            {
                this.hayGolem.getNavigation().moveTo((double)((float)this.destinationBlock.getX()) + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)((float)this.destinationBlock.getZ()) + 0.5D, this.movementSpeed);
            }
        }
        else
        {
            this.closeToDestination = true;
            //this.hayGolem.farm.focusedBlocks.remove(this.destinationBlock);
            --this.timeoutCounter;
        }

        if (this.closeToDestination)
        {
            this.hayGolem.getLookControl().setLookAt((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, 10.0F, (float)this.hayGolem.getMaxHeadXRot());

            Level world = this.hayGolem.level;
            BlockPos blockpos = this.destinationBlock.above();
            BlockState iblockstate = world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();

            if (this.currentTask == Task.USE_BONEMEAL && block instanceof CropBlock)
            {
                for (int i = 0; i < this.hayGolem.getInventory().getContainerSize(); i++)
                {
                    ItemStack itemStack = this.hayGolem.getInventory().getItem(i);

                    if (!itemStack.isEmpty() && itemStack.getItem() == Items.BONE_MEAL)
                    {
                        ((CropBlock) block).growCrops(world, blockpos, iblockstate);

                        itemStack.shrink(1);
                        if (itemStack.isEmpty())
                        {
                            this.hayGolem.getInventory().setItem(i, ItemStack.EMPTY);
                        }
                        break;
                    }
                }
            }
            else if (this.currentTask == Task.HARVEST_CROPS && block instanceof CropBlock && ((CropBlock)block).isMaxAge(iblockstate))
            {
                List<ItemStack> drops = iblockstate.getBlock().getDrops(iblockstate, (ServerLevel) world, blockpos, null);
                for (ItemStack drop : drops)
                    this.hayGolem.getInventory().addItem(drop);

                world.destroyBlock(blockpos, false);
            }
            else if (this.currentTask == Task.PLANT_CROPS && iblockstate.getMaterial() == Material.AIR)
            {
                SimpleContainer inventorybasic = this.hayGolem.getInventory();

                for (int i = 0; i < inventorybasic.getContainerSize(); ++i)
                {
                    ItemStack itemstack = inventorybasic.getItem(i);
                    boolean usedItem = false;

                    if (!itemstack.isEmpty())
                    {
                        if (itemstack.getItem() == Items.WHEAT_SEEDS)
                        {
                            world.setBlock(blockpos, Blocks.WHEAT.defaultBlockState(), 3);
                            usedItem = true;
                        }
                        else if (itemstack.getItem() == Items.POTATO)
                        {
                            world.setBlock(blockpos, Blocks.POTATOES.defaultBlockState(), 3);
                            usedItem = true;
                        }
                        else if (itemstack.getItem() == Items.CARROT)
                        {
                            world.setBlock(blockpos, Blocks.CARROTS.defaultBlockState(), 3);
                            usedItem = true;
                        }
                        else if (itemstack.getItem() == Items.BEETROOT_SEEDS)
                        {
                            world.setBlock(blockpos, Blocks.BEETROOTS.defaultBlockState(), 3);
                            usedItem = true;
                        }
                    }

                    if (usedItem)
                    {
                        itemstack.shrink(1);

                        if (itemstack.isEmpty())
                        {
                            inventorybasic.setItem(i, ItemStack.EMPTY);
                        }

                        break;
                    }
                }
            }
            else if (this.currentTask == Task.HARVEST_SPECIFIC_BLOCKS && (block instanceof MelonBlock || block instanceof PumpkinBlock))
            {
                List<ItemStack> drops = iblockstate.getBlock().getDrops(iblockstate, (ServerLevel) world, blockpos, null);
                for (ItemStack drop : drops)
                    this.hayGolem.getInventory().addItem(drop);

                world.destroyBlock(blockpos, false);
            }
            else if ((this.currentTask == Task.PLANT_REEDS || this.currentTask == Task.PLANT_CACTUS) && iblockstate.getMaterial() == Material.AIR &&
                    (this.currentTask == Task.PLANT_CACTUS ? !(world.getBlockState(blockpos.north()).getMaterial().blocksMotion() ||
                            world.getBlockState(blockpos.south()).getMaterial().blocksMotion() ||
                            world.getBlockState(blockpos.west()).getMaterial().blocksMotion() ||
                            world.getBlockState(blockpos.east()).getMaterial().blocksMotion()) : true))
            {
                SimpleContainer inventorybasic = this.hayGolem.getInventory();

                for (int i = 0; i < inventorybasic.getContainerSize(); ++i)
                {
                    ItemStack itemstack = inventorybasic.getItem(i);
                    boolean usedItem = false;

                    if (!itemstack.isEmpty())
                    {
                        if (itemstack.getItem() == Items.SUGAR_CANE && this.currentTask == Task.PLANT_REEDS)
                        {
                            world.setBlock(blockpos, Blocks.SUGAR_CANE.defaultBlockState(), 3);
                            usedItem = true;
                        }
                        else if (itemstack.getItem() == Item.byBlock(Blocks.CACTUS) && this.currentTask == Task.PLANT_CACTUS)
                        {
                            world.setBlock(blockpos, Blocks.CACTUS.defaultBlockState(), 3);
                            usedItem = true;
                        }
                    }

                    if (usedItem)
                    {
                        itemstack.shrink(1);

                        if (itemstack.isEmpty())
                        {
                            inventorybasic.setItem(i, ItemStack.EMPTY);
                        }

                        break;
                    }
                }
            }
            else if ((this.currentTask == Task.HARVEST_REEDS || this.currentTask == Task.HARVEST_CACTUS) &&
                    this.currentTask == Task.HARVEST_REEDS ? world.getBlockState(blockpos.above()).getBlock() instanceof SugarCaneBlock
                    : world.getBlockState(blockpos.above()).getBlock() instanceof CactusBlock)
            {
                for (int i = 3; i > 0; i--)
                {
                    BlockPos iblockpos = blockpos.above(i);
                    if (!(world.getBlockState(iblockpos).getBlock() instanceof SugarCaneBlock || world.getBlockState(iblockpos).getBlock() instanceof CactusBlock))
                        continue;

                    List<ItemStack> drops = iblockstate.getBlock().getDrops(iblockstate, (ServerLevel) world, blockpos, null);
                    for (ItemStack drop : drops)
                        this.hayGolem.getInventory().addItem(drop);

                    world.destroyBlock(iblockpos, false);
                }
            }
            else if (this.currentTask == Task.DEPOSIT_CHEST && block instanceof ChestBlock)
            {
                ChestBlockEntity chest = (ChestBlockEntity) world.getBlockEntity(blockpos);
                IItemHandler itemhandler = chest.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.NORTH).resolve().get();

                boolean flagCactusReeds = false;

                for (int i = 0; i < this.hayGolem.getInventory().getContainerSize(); i++)
                {
                    ItemStack itemStack = this.hayGolem.getInventory().getItem(i);
                    if (itemStack.isEmpty())
                        continue;

                    boolean flagMoved = false;
                    if (!HayGolemEntity.USABLE_TOOLS.contains(itemStack.getItem()) && !(itemStack.getItem() == Items.WHEAT_SEEDS
                            || itemStack.getItem() == Items.POTATO
                            || itemStack.getItem() == Items.CARROT
                            || itemStack.getItem() == Items.BEETROOT_SEEDS
                            || itemStack.getItem() == Items.SUGAR_CANE
                            || itemStack.getItem() == Item.byBlock(Blocks.CACTUS)))
                    {
                        for (int n = 0; n < itemhandler.getSlots(); n++)
                        {
                            if (itemhandler.getStackInSlot(n).isEmpty())
                            {
                                itemhandler.insertItem(n, itemStack, false);
                                flagMoved = true;
                                break;
                            }
                        }
                    }
                    else if (itemStack.getItem() == Items.SUGAR_CANE || itemStack.getItem() == Item.byBlock(Blocks.CACTUS) && !flagCactusReeds)
                    {
                        for (int n = 0; n < itemhandler.getSlots(); n++)
                        {
                            if (itemhandler.getStackInSlot(n).isEmpty())
                            {
                                itemhandler.insertItem(n, itemStack, false);
                                flagCactusReeds = true;
                                flagMoved = true;
                                break;
                            }
                        }
                    }

                    if (flagMoved)
                        this.hayGolem.getInventory().setItem(i, ItemStack.EMPTY);
                }
            }

            FarmManager.findByUuid(this.hayGolem.farm).focusedBlocks.remove(this.destinationBlock);
            this.currentTask = Task.IDLING;
            this.runDelay = 10;
        }
    }

    protected boolean getCloseToDestination()
    {
        return this.closeToDestination;
    }

    private boolean searchForDestination()
    {
        BlockPos startingPos = FarmManager.findByUuid(this.hayGolem.farm).getStartingPos();
        Vec3i farmSize = FarmManager.findByUuid(this.hayGolem.farm).getSize();
        BlockPos endingPos = startingPos.offset(FarmManager.findByUuid(this.hayGolem.farm).getSize());

        for (int x = startingPos.getX(); farmSize.getX() < 0 ? x >= endingPos.getX() : x <= endingPos.getX(); x += farmSize.getX() < 0 ? -1 : 1)
        {
            for (int y = startingPos.getY() - 1; farmSize.getY() < 0 ? y >= endingPos.getY() : y <= endingPos.getY(); y += farmSize.getY() < 0 ? -1 : 1)
            {
                for (int z = startingPos.getZ(); farmSize.getZ() < 0 ? z >= endingPos.getZ() : z <= endingPos.getZ(); z += farmSize.getZ() < 0 ? -1 : 1)
                {
                    BlockPos pos = new BlockPos(x, y, z);

                    if (!FarmManager.findByUuid(this.hayGolem.farm).focusedBlocks.contains(pos) && this.shouldMoveTo(this.hayGolem.level, pos))
                    {
                        this.destinationBlock = pos;
                        FarmManager.findByUuid(this.hayGolem.farm).focusedBlocks.add(this.destinationBlock);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean shouldMoveTo(Level worldIn, BlockPos pos)
    {
        BlockState blockState = worldIn.getBlockState(pos);
        Block block = blockState.getBlock();

        BlockState topBlockState = worldIn.getBlockState(pos.above());
        Block topBlock = topBlockState.getBlock();


        boolean moveToFlag = false;
        if (block instanceof FarmBlock)
        {
            if (topBlock instanceof CropBlock && ((CropBlock)topBlock).isMaxAge(topBlockState) && (this.currentTask == Task.IDLING || this.currentTask == Task.HARVEST_CROPS))
            {
                this.currentTask = Task.HARVEST_CROPS;
                moveToFlag = true;
            }
            else if (topBlock instanceof CropBlock && this.hayGolem.hasItem(Items.BONE_MEAL) && (this.currentTask == Task.IDLING || this.currentTask == Task.USE_BONEMEAL))
            {
                this.currentTask = Task.USE_BONEMEAL;
                moveToFlag = true;
            }
            if (topBlockState.getMaterial() == Material.AIR && this.hayGolem.hasPlantableItem() && (this.currentTask == Task.IDLING || this.currentTask == Task.PLANT_CROPS))
            {
                this.currentTask = Task.PLANT_CROPS;
                moveToFlag = true;
            }
        }
        else if ((topBlock instanceof MelonBlock || topBlock instanceof PumpkinBlock) && (this.currentTask == Task.IDLING || this.currentTask == Task.HARVEST_SPECIFIC_BLOCKS))
        {
            this.currentTask = Task.HARVEST_SPECIFIC_BLOCKS;
            moveToFlag = true;
        }
        else if (topBlock instanceof ChestBlock && worldIn.getBlockEntity(pos.above()) instanceof ChestBlockEntity && this.hayGolem.isInventoryFull() && (this.currentTask == Task.IDLING || this.currentTask == Task.DEPOSIT_CHEST))
        {
            ChestBlockEntity chestEntity = (ChestBlockEntity) worldIn.getBlockEntity(pos.above());

            // check if chest is full
            boolean full = true;
            for (int i = 0; i < chestEntity.getContainerSize(); i++)
            {
                if (chestEntity.getItem(i).isEmpty())
                    full = false;
            }

            // if the chest is not full, then the golem can go deposit to it
            if (!full)
            {
                this.currentTask = Task.DEPOSIT_CHEST;
                moveToFlag = true;
            }
        }
        else if (block instanceof GrassBlock &&
                (worldIn.getBlockState(pos.north()).getMaterial() == Material.WATER ||
                        worldIn.getBlockState(pos.south()).getMaterial() == Material.WATER ||
                        worldIn.getBlockState(pos.west()).getMaterial() == Material.WATER ||
                        worldIn.getBlockState(pos.east()).getMaterial() == Material.WATER))
        {
            if (topBlock instanceof SugarCaneBlock && worldIn.getBlockState(pos.above().above()).getBlock() instanceof SugarCaneBlock && (this.currentTask == Task.IDLING || this.currentTask == Task.HARVEST_REEDS))
            {
                this.currentTask = Task.HARVEST_REEDS;
                moveToFlag = true;
            }
            if (topBlockState.getMaterial() == Material.AIR && this.hayGolem.hasItem(Items.SUGAR_CANE) && (this.currentTask == Task.IDLING || this.currentTask == Task.PLANT_REEDS))
            {
                this.currentTask = Task.PLANT_REEDS;
                moveToFlag = true;
            }
        }
        else if (block instanceof SandBlock &&
                !(worldIn.getBlockState(pos.above().north()).getMaterial().blocksMotion() ||
                        worldIn.getBlockState(pos.above().south()).getMaterial().blocksMotion() ||
                        worldIn.getBlockState(pos.above().west()).getMaterial().blocksMotion() ||
                        worldIn.getBlockState(pos.above().east()).getMaterial().blocksMotion()))
        {
            if (topBlock instanceof CactusBlock && worldIn.getBlockState(pos.above().above()).getBlock() instanceof CactusBlock && (this.currentTask == Task.IDLING || this.currentTask == Task.HARVEST_CACTUS))
            {
                this.currentTask = Task.HARVEST_CACTUS;
                moveToFlag = true;
            }
            if (topBlockState.getMaterial() == Material.AIR && this.hayGolem.hasItem(Item.byBlock(Blocks.CACTUS)) && (this.currentTask == Task.IDLING || this.currentTask == Task.PLANT_CACTUS))
            {
                this.currentTask = Task.PLANT_CACTUS;
                moveToFlag = true;
            }
        }

        //if (moveToFlag)
        //    this.hayGolem.farm.focusedBlocks.add(pos);

        return moveToFlag;
    }
}
