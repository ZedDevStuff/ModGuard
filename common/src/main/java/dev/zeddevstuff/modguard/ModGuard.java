package dev.zeddevstuff.modguard;

/// In theory everything in the root package should work no matter the version of Minecraft
/// The stuff in client and mixin is another story
public final class ModGuard
{
    public static final String MOD_ID = "modguard";
    private static ModGuardConfig config;
    public static ModGuardConfig getConfig() { return config; }

    public static void init()
    {
        // TODO: Load the config file
        config = new ModGuardConfig();
    }
}
