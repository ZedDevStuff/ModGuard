package dev.zeddevstuff.modguard;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.List;

public class LoaderUtils
{
    static List<ModEntry> debugModlist;
    @ExpectPlatform
    public static List<ModEntry> getMods()
    {
        return debugModlist;
    }

    public record ModEntry(String id, String version) {};
}
