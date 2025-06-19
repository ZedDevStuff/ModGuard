package dev.zeddevstuff.modguard;

public class ModGuardConfig
{
    public ChangeType changeType = ChangeType.PATCH;

    public boolean notifyModRemovals = true;
    public boolean notifyModAdditions = true;

    public enum ChangeType
    {
        MAJOR,
        MINOR,
        PATCH
    }
}
