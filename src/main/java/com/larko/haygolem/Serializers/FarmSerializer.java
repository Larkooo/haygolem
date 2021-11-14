package com.larko.haygolem.Serializers;

import com.larko.haygolem.Util.Metadata;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

abstract class BaseSaveData extends WorldSavedData {

    public NBTTagCompound data = new NBTTagCompound();

    public BaseSaveData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        data = nbt;
    }
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = data;
        return compound;
    }

}

public class FarmSerializer extends BaseSaveData {

    private static final String DATA_NAME = Metadata.MODID + "_FarmsData";

    public FarmSerializer() {
        super(DATA_NAME);
    }

    public FarmSerializer(String s) {
        super(s);
    }

    public static FarmSerializer get() {
        World world = DimensionManager.getWorld(0);
        FarmSerializer save = (FarmSerializer) world.getMapStorage().getOrLoadData(FarmSerializer.class, DATA_NAME);
        if(save == null) {
            save = new FarmSerializer();
            world.getMapStorage().setData(DATA_NAME, save);
        }
        return save;
    }

}
