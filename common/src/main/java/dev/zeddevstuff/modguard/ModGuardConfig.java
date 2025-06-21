package dev.zeddevstuff.modguard;

public class ModGuardConfig
{
    public ChangeType changeType = ChangeType.MINOR_OR_GREATER;

    public boolean notifyModRemovals = true;
    public boolean notifyModAdditions = true;

    public enum ChangeType
    {
        MAJOR_ONLY,
        MINOR_OR_GREATER,
        PATCH_OR_GREATER
    }
}
