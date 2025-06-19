package dev.zeddevstuff.modguard.fabric;

import dev.zeddevstuff.modguard.LoaderUtils;

import java.util.List;

public class LoaderUtilsImpl
{
    public static List<LoaderUtils.ModEntry> getMods() {
        return net.fabricmc.loader.api.FabricLoader.getInstance()
            .getAllMods()
            .stream()
            .map(mod -> new LoaderUtils.ModEntry(mod.getMetadata().getId(), mod.getMetadata().getVersion().getFriendlyString()))
            .toList();
    }
}
