package dev.zeddevstuff.modguard;

import java.util.Objects;
import java.util.Optional;

public class VersionRange
{
    public Version MinVersion;
    public Version MaxVersion;
    public boolean IncludeMinVersion;
    public boolean IncludeMaxVersion;
    public boolean ignorePreRelease;

    public VersionRange(Version minVersion, Version maxVersion)
    {
        this(minVersion, maxVersion, true, false);
    }
    public VersionRange(Version minVersion, Version maxVersion, boolean includeMinVersion, boolean includeMaxVersion)
    {
        MinVersion = minVersion;
        MaxVersion = maxVersion;
        IncludeMinVersion = includeMinVersion;
        IncludeMaxVersion = includeMaxVersion;
    }

    public boolean contains(Version version)
{
    return IncludeMinVersion
        && version.majorMinorPatchEquals(MinVersion) || (IncludeMaxVersion
        && version.majorMinorPatchEquals(MaxVersion) || version.greaterThan(MinVersion, ignorePreRelease)
        && version.lessThan(MaxVersion, ignorePreRelease));
}

    public static VersionRange parse(String input)
    {
        Version minVersion = Version.min;
        Version maxVersion = Version.max;
        boolean includeMinVersion = true;
        boolean includeMaxVersion = true;
        if (!(input.startsWith("[") || input.startsWith("(")) && !(input.endsWith("]") || input.endsWith(")")))
        {
            if (input.startsWith(">="))
            {
                //Java equivalent of minVersion = Version.parse(input[2..]);
                // where [2..] is a substring from index 2 to the end
                minVersion = Version.parse(input.substring(2));
            }
            else if (input.startsWith(">"))
            {
                minVersion = Version.parse(input.substring(1));
                includeMinVersion = false;
            }
            else if (input.startsWith("<="))
            {
                maxVersion = Version.parse(input.substring(2));
            }
            else if (input.startsWith("<"))
            {
                maxVersion = Version.parse(input.substring(1));
                includeMaxVersion = false;
            }
            else
            {
                minVersion = Version.parse(input);
                maxVersion = minVersion;
                includeMinVersion = true;
                includeMaxVersion = true;
            }
        }
        else
        {
            if (input.startsWith("["))
                includeMinVersion = true;
            else if (input.startsWith("("))
                includeMinVersion = false;
            if (input.endsWith("]"))
                includeMaxVersion = true;
            else if (input.endsWith(")"))
                includeMaxVersion = false;
            var versions = input.substring(1, input.length() - 1).split(",");
            if (versions.length != 2)
            {
                throw new IllegalArgumentException("Invalid version range format: " + input);
            }
            minVersion = Version.parse(versions[0].trim());
            maxVersion = Version.parse(versions[1].trim());
        }
        return new VersionRange(minVersion, maxVersion, includeMinVersion, includeMaxVersion);
    }
    public static Optional<VersionRange> tryParse(String input)
    {
        try
        {
            return Optional.of(parse(input));
        }
        catch(Exception ignored)
        {
            return Optional.empty();
        }
    }

    public boolean equals(Object obj)
    {
        return obj instanceof VersionRange range
            && MinVersion == range.MinVersion
            && MaxVersion == range.MaxVersion
            && IncludeMinVersion == range.IncludeMinVersion
            && IncludeMaxVersion == range.IncludeMaxVersion;
    }

    public int hashCode()
    {
    return Objects.hash(MinVersion, MaxVersion, IncludeMinVersion, IncludeMaxVersion);
    }

    public String toString()
    {
        return (IncludeMinVersion ? "[" : "(") + MinVersion + "," + MaxVersion + (IncludeMaxVersion ? "]" : ")");
    }
}
