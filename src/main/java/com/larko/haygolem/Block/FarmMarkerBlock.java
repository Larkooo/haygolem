package com.larko.haygolem.Block;

import com.larko.haygolem.Util.Metadata;
import net.minecraft.block.BlockTorch;

public class FarmMarkerBlock extends BlockTorch
{
    public FarmMarkerBlock()
    {
        super();
        setRegistryName(Metadata.MODID, "farm_marker");
        setUnlocalizedName("FarmMarker");
    }
}