package com.larko.haygolem.Serializers;

import com.larko.haygolem.Managers.FarmManager;
import com.larko.haygolem.Util.Metadata;
import com.larko.haygolem.World.Farm;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;


import java.util.HashSet;
import java.util.Set;

public class FarmSerializer extends SavedData {
    private static final String DATA_NAME = Metadata.MODID + "_FarmsData";

    public FarmSerializer(CompoundTag data)
    {
        if (data != null)
        {
            for (String key : data.getAllKeys())
            {
                Farm farm = Farm.deserialize(data.getCompound(key));

                FarmManager.farms.add(farm);
            }
        }
    }
    public FarmSerializer() {}

    @Override
    public CompoundTag save(CompoundTag data) {
        // remove all data
        if(data != null && !data.isEmpty()) {
            Set<String> toRemove = new HashSet<String>();
            for(String key : data.getAllKeys()) { // Remove all data
                if(!key.equals("")) {
                    toRemove.add(key);
                }
            }
            for(String key : toRemove) {
                data.remove(key);
            }
        }

        // patch new data
        for (Farm farm : FarmManager.farms)
        {
            CompoundTag farmData = farm.serialize();

            data.put("FARM_" + farm.getUuid().toString(), farmData);
        }

        return data;
    }

     public static FarmSerializer get(MinecraftServer server)
     {
         ServerLevel level = server.getLevel(Level.OVERWORLD);
         return level.getDataStorage().computeIfAbsent(FarmSerializer::new, FarmSerializer::new, DATA_NAME);
     }
}
