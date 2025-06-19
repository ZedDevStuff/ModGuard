package dev.zeddevstuff.modguard.neoforge;

import dev.zeddevstuff.modguard.LoaderUtils;
import net.neoforged.fml.ModList;

import java.util.List;

public class LoaderUtilsImpl
{
    public static List<LoaderUtils.ModEntry> getMods()
    {
        return ModList.get()
            .getMods()
            .stream().map(
            mod -> new LoaderUtils.ModEntry(mod.getModId(), mod.getVersion().toString())
        )
            .filter(mod -> !mod.id().startsWith("modguard") && !mod.id().startsWith("generated") && !(mod.id().equals("neoforge")))
            .toList();
    }
}
