package com.larko.haygolem.Entity.AI;

import com.larko.haygolem.Entity.HayGolemEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.swing.plaf.basic.BasicComboBoxUI;
import java.util.List;

public class HayGolemHarvestAI extends EntityAIBase
{
    private final HayGolemEntity hayGolem;
    private final double movementSpeed;
    protected int runDelay;
    private int timeoutCounter;
    private int maxStayTicks;
    protected BlockPos destinationBlock = BlockPos.ORIGIN;
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
        this.setMutexBits(5);
    }

    public boolean shouldExecute()
    {
        if (this.runDelay > 0)
        {
            --this.runDelay;
            return false;
        }
        else
        {
            this.runDelay = 10 + this.hayGolem.getRNG().nextInt(10);
            return this.hayGolem.farm != null && this.searchForDestination();
        }
    }

    public boolean shouldContinueExecuting()
    {
        return this.hayGolem.farm != null && this.currentTask != Task.IDLING && this.timeoutCounter >= -this.maxStayTicks && this.timeoutCounter <= 1200 && this.shouldMoveTo(this.hayGolem.world, this.destinationBlock);
    }

    public void startExecuting()
    {
        this.hayGolem.getNavigator().tryMoveToXYZ((double)((float)this.destinationBlock.getX()) + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)((float)this.destinationBlock.getZ()) + 0.5D, this.movementSpeed);
        this.timeoutCounter = 0;
        this.maxStayTicks = this.hayGolem.getRNG().nextInt(this.hayGolem.getRNG().nextInt(1200) + 1200) + 1200;
    }

    public void updateTask()
    {
        if (Math.sqrt(this.hayGolem.getDistanceSq(this.destinationBlock.up())) > 3.0)
        {
            this.closeToDestination = false;
            ++this.timeoutCounter;

            if (this.timeoutCounter % 40 == 0)
            {
                this.hayGolem.getNavigator().tryMoveToXYZ((double)((float)this.destinationBlock.getX()) + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)((float)this.destinationBlock.getZ()) + 0.5D, this.movementSpeed);
            }
        }
        else
        {
            this.closeToDestination = true;
            --this.timeoutCounter;
        }

        if (this.closeToDestination)
        {
            this.hayGolem.getLookHelper().setLookPosition((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, 10.0F, (float)this.hayGolem.getVerticalFaceSpeed());

            World world = this.hayGolem.world;
            BlockPos blockpos = this.destinationBlock.up();
            IBlockState iblockstate = world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();

            if (this.currentTask == Task.USE_BONEMEAL && block instanceof BlockCrops)
            {
                for (int i = 0; i < this.hayGolem.getInventory().getSizeInventory(); i++)
                {
                    ItemStack itemStack = this.hayGolem.getInventory().getStackInSlot(i);

                    if (!itemStack.isEmpty() && itemStack.getItem() == Items.DYE && itemStack.getMetadata() == 15)
                    {
                        ((BlockCrops) block).grow(world, blockpos, iblockstate);

                        itemStack.shrink(1);
                        if (itemStack.isEmpty())
                        {
                            this.hayGolem.getInventory().setInventorySlotContents(i, ItemStack.EMPTY);
                        }
                        break;
                    }
                }
            }
            else if (this.currentTask == Task.HARVEST_CROPS && block instanceof BlockCrops && ((BlockCrops)block).isMaxAge(iblockstate))
            {
                List<ItemStack> drops = world.getBlockState(blockpos).getBlock().getDrops(world, blockpos, world.getBlockState(blockpos), 0);
                for (ItemStack drop : drops)
                    this.hayGolem.getInventory().addItem(drop);

                world.destroyBlock(blockpos, false);
            }
            else if (this.currentTask == Task.PLANT_CROPS && iblockstate.getMaterial() == Material.AIR)
            {
                InventoryBasic inventorybasic = this.hayGolem.getInventory();

                for (int i = 0; i < inventorybasic.getSizeInventory(); ++i)
                {
                    ItemStack itemstack = inventorybasic.getStackInSlot(i);
                    boolean usedItem = false;

                    if (!itemstack.isEmpty())
                    {
                        if (itemstack.getItem() == Items.WHEAT_SEEDS)
                        {
                            world.setBlockState(blockpos, Blocks.WHEAT.getDefaultState(), 3);
                            usedItem = true;
                        }
                        else if (itemstack.getItem() == Items.POTATO)
                        {
                            world.setBlockState(blockpos, Blocks.POTATOES.getDefaultState(), 3);
                            usedItem = true;
                        }
                        else if (itemstack.getItem() == Items.CARROT)
                        {
                            world.setBlockState(blockpos, Blocks.CARROTS.getDefaultState(), 3);
                            usedItem = true;
                        }
                        else if (itemstack.getItem() == Items.BEETROOT_SEEDS)
                        {
                            world.setBlockState(blockpos, Blocks.BEETROOTS.getDefaultState(), 3);
                            usedItem = true;
                        }
                    }

                    if (usedItem)
                    {
                        itemstack.shrink(1);

                        if (itemstack.isEmpty())
                        {
                            inventorybasic.setInventorySlotContents(i, ItemStack.EMPTY);
                        }

                        break;
                    }
                }
            }
            else if (this.currentTask == Task.HARVEST_SPECIFIC_BLOCKS && (block instanceof BlockMelon || block instanceof BlockPumpkin))
            {
                List<ItemStack> drops = world.getBlockState(blockpos).getBlock().getDrops(world, blockpos, world.getBlockState(blockpos), 0);
                for (ItemStack drop : drops)
                    this.hayGolem.getInventory().addItem(drop);

                world.destroyBlock(blockpos, false);
            }
            else if ((this.currentTask == Task.PLANT_REEDS || this.currentTask == Task.PLANT_CACTUS) && iblockstate.getMaterial() == Material.AIR &&
                    (this.currentTask == Task.PLANT_CACTUS ? !(world.getBlockState(blockpos.north()).getMaterial().blocksMovement() ||
                            world.getBlockState(blockpos.south()).getMaterial().blocksMovement() ||
                            world.getBlockState(blockpos.west()).getMaterial().blocksMovement() ||
                            world.getBlockState(blockpos.east()).getMaterial().blocksMovement()) : true))
            {
                InventoryBasic inventorybasic = this.hayGolem.getInventory();

                for (int i = 0; i < inventorybasic.getSizeInventory(); ++i)
                {
                    ItemStack itemstack = inventorybasic.getStackInSlot(i);
                    boolean usedItem = false;

                    if (!itemstack.isEmpty())
                    {
                        if (itemstack.getItem() == Items.REEDS && this.currentTask == Task.PLANT_REEDS)
                        {
                            world.setBlockState(blockpos, Blocks.REEDS.getDefaultState(), 3);
                            usedItem = true;
                        }
                        else if (itemstack.getItem() == Item.getItemFromBlock(Blocks.CACTUS) && this.currentTask == Task.PLANT_CACTUS)
                        {
                            world.setBlockState(blockpos, Blocks.CACTUS.getDefaultState(), 3);
                            usedItem = true;
                        }
                    }

                    if (usedItem)
                    {
                        itemstack.shrink(1);

                        if (itemstack.isEmpty())
                        {
                            inventorybasic.setInventorySlotContents(i, ItemStack.EMPTY);
                        }

                        break;
                    }
                }
            }
            else if ((this.currentTask == Task.HARVEST_REEDS || this.currentTask == Task.HARVEST_CACTUS) &&
                    this.currentTask == Task.HARVEST_REEDS ? world.getBlockState(blockpos.up()).getBlock() instanceof BlockReed
                    : world.getBlockState(blockpos.up()).getBlock() instanceof BlockCactus)
            {
                for (int i = 3; i > 0; i--)
                {
                    BlockPos iblockpos = blockpos.up(i);
                    if (!(world.getBlockState(iblockpos).getBlock() instanceof BlockReed || world.getBlockState(iblockpos).getBlock() instanceof BlockCactus))
                        continue;

                    List<ItemStack> drops = world.getBlockState(iblockpos).getBlock().getDrops(world, iblockpos, world.getBlockState(iblockpos), 0);
                    for (ItemStack drop : drops)
                        this.hayGolem.getInventory().addItem(drop);

                    world.destroyBlock(iblockpos, false);
                }
            }
            else if (this.currentTask == Task.DEPOSIT_CHEST && block instanceof BlockChest)
            {
                TileEntityChest chest = (TileEntityChest) world.getTileEntity(blockpos);
                IItemHandler itemhandler = chest.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);

                boolean flagCactusReeds = false;

                for (int i = 0; i < this.hayGolem.getInventory().getSizeInventory(); i++)
                {
                    ItemStack itemStack = this.hayGolem.getInventory().getStackInSlot(i);
                    if (itemStack.isEmpty())
                        continue;

                    boolean flagMoved = false;
                    if (!HayGolemEntity.USABLE_TOOLS.contains(itemStack.getItem()) && !(itemStack.getItem() == Items.WHEAT_SEEDS
                            || itemStack.getItem() == Items.POTATO
                            || itemStack.getItem() == Items.CARROT
                            || itemStack.getItem() == Items.BEETROOT_SEEDS
                            || itemStack.getItem() == Items.REEDS
                            || itemStack.getItem() == Item.getItemFromBlock(Blocks.CACTUS)))
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
                    else if (itemStack.getItem() == Items.REEDS || itemStack.getItem() == Item.getItemFromBlock(Blocks.CACTUS) && !flagCactusReeds)
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
                        this.hayGolem.getInventory().removeStackFromSlot(i);
                }
            }

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
        BlockPos startingPos = this.hayGolem.farm.getStartingPos();
        Vec3i farmSize = this.hayGolem.farm.getSize();
        BlockPos endingPos = startingPos.add(this.hayGolem.farm.getSize());

        for (int x = startingPos.getX(); farmSize.getX() < 0 ? x >= endingPos.getX() : x <= endingPos.getX(); x += farmSize.getX() < 0 ? -1 : 1)
        {
            for (int y = startingPos.getY() - 1; farmSize.getY() < 0 ? y >= endingPos.getY() : y <= endingPos.getY(); y += farmSize.getY() < 0 ? -1 : 1)
            {
                for (int z = startingPos.getZ(); farmSize.getZ() < 0 ? z >= endingPos.getZ() : z <= endingPos.getZ(); z += farmSize.getZ() < 0 ? -1 : 1)
                {
                    BlockPos pos = new BlockPos(x, y, z);

                    if (this.shouldMoveTo(this.hayGolem.world, pos))
                    {
                        destinationBlock = pos;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean shouldMoveTo(World worldIn, BlockPos pos)
    {
        IBlockState blockState = worldIn.getBlockState(pos);
        Block block = blockState.getBlock();

        IBlockState topBlockState = worldIn.getBlockState(pos.up());
        Block topBlock = topBlockState.getBlock();

        if (block instanceof BlockFarmland)
        {
            if (topBlock instanceof BlockCrops && ((BlockCrops)topBlock).isMaxAge(topBlockState) && (this.currentTask == Task.IDLING || this.currentTask == Task.HARVEST_CROPS))
            {
                this.currentTask = Task.HARVEST_CROPS;
                return true;
            }
            else if (topBlock instanceof BlockCrops && this.hayGolem.hasItem(Items.DYE)  && this.hayGolem.getItem(Items.DYE).getMetadata() == 15 && (this.currentTask == Task.IDLING || this.currentTask == Task.USE_BONEMEAL))
            {
                this.currentTask = Task.USE_BONEMEAL;
                return true;
            }
            if (topBlockState.getMaterial() == Material.AIR && this.hayGolem.hasPlantableItem() && (this.currentTask == Task.IDLING || this.currentTask == Task.PLANT_CROPS))
            {
                this.currentTask = Task.PLANT_CROPS;
                return true;
            }
        }
        else if ((topBlock instanceof BlockMelon || topBlock instanceof BlockPumpkin) && (this.currentTask == Task.IDLING || this.currentTask == Task.HARVEST_SPECIFIC_BLOCKS))
        {
            this.currentTask = Task.HARVEST_SPECIFIC_BLOCKS;
            return true;
        }
        else if (topBlock instanceof BlockChest && this.hayGolem.isInventoryFull() && (this.currentTask == Task.IDLING || this.currentTask == Task.DEPOSIT_CHEST))
        {
            this.currentTask = Task.DEPOSIT_CHEST;
            return true;
        }
        else if (block instanceof BlockGrass &&
                (worldIn.getBlockState(pos.north()).getMaterial() == Material.WATER ||
                        worldIn.getBlockState(pos.south()).getMaterial() == Material.WATER ||
                        worldIn.getBlockState(pos.west()).getMaterial() == Material.WATER ||
                        worldIn.getBlockState(pos.east()).getMaterial() == Material.WATER))
        {
            if (topBlock instanceof BlockReed && worldIn.getBlockState(pos.up().up()).getBlock() instanceof BlockReed && (this.currentTask == Task.IDLING || this.currentTask == Task.HARVEST_REEDS))
            {
                this.currentTask = Task.HARVEST_REEDS;
                return true;
            }
            if (topBlockState.getMaterial() == Material.AIR && this.hayGolem.hasItem(Items.REEDS) && (this.currentTask == Task.IDLING || this.currentTask == Task.PLANT_REEDS))
            {
                this.currentTask = Task.PLANT_REEDS;
                return true;
            }
        }
        else if (block instanceof BlockSand &&
                !(worldIn.getBlockState(pos.up().north()).getMaterial().blocksMovement() ||
                        worldIn.getBlockState(pos.up().south()).getMaterial().blocksMovement() ||
                        worldIn.getBlockState(pos.up().west()).getMaterial().blocksMovement() ||
                        worldIn.getBlockState(pos.up().east()).getMaterial().blocksMovement()))
        {
            if (topBlock instanceof BlockCactus && worldIn.getBlockState(pos.up().up()).getBlock() instanceof BlockCactus && (this.currentTask == Task.IDLING || this.currentTask == Task.HARVEST_CACTUS))
            {
                this.currentTask = Task.HARVEST_CACTUS;
                return true;
            }
            if (topBlockState.getMaterial() == Material.AIR && this.hayGolem.hasItem(Item.getItemFromBlock(Blocks.CACTUS)) && (this.currentTask == Task.IDLING || this.currentTask == Task.PLANT_CACTUS))
            {
                this.currentTask = Task.PLANT_CACTUS;
                return true;
            }
        }

        return false;
    }
}
