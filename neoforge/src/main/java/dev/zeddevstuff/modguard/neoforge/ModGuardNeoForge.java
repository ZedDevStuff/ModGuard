package dev.zeddevstuff.modguard.neoforge;

import dev.zeddevstuff.modguard.ModGuard;
import net.neoforged.fml.common.Mod;

@Mod(ModGuard.MOD_ID)
public final class ModGuardNeoForge
{
    public ModGuardNeoForge()
    {
        // Run our common setup.
        ModGuard.init();
    }
}
