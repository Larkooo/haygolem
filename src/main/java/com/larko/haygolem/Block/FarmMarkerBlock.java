package com.larko.haygolem.Block;

import com.larko.haygolem.Util.Metadata;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;

public class FarmMarkerBlock extends BlockTorch
{
    public FarmMarkerBlock()
    {
        super();
        setRegistryName("farm_marker");
        setUnlocalizedName("FarmMarker");
        setHardness(0.0F);
        setLightLevel(0.9375F);
        setSoundType(SoundType.WOOD);
    }

}