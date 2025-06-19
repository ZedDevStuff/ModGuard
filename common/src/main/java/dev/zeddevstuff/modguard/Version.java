package dev.zeddevstuff.modguard;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version
{
    public static Pattern SEMVER_REGEX = Pattern.compile("(?<major>0|[1-9]\\d*)\\.(?<minor>0|[1-9]\\d*)\\.(?<patch>0|[1-9]\\d*)(?:-(?<prerelease>(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+(?<buildmetadata>[0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?");
    public static String regexGroup(Matcher matcher, String groupName)
    {
        try { return matcher.group(groupName); }
        catch (IllegalArgumentException e) { return ""; }
    }
    private String versionString = "";
    private final int major;
    public int getMajor() { return major; }
    private final int minor;
    public int getMinor() { return minor; }
    private final int patch;
    public int getPatch() { return patch; }
    public String preRelease;
    public String getPreRelease() { return preRelease; }
    public String metadata;
    public String getMetadata() { return metadata; }

    public Version(int major, int minor, int patch)
    {
        this(major, minor, patch, "", "");
    }
    public Version(int major, int minor, int patch, String prerelease, String metadata)
    {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        preRelease = prerelease;
        this.metadata = metadata;
    }

    public static Version parse(String input)
    {
        input = input.trim();
        var match = SEMVER_REGEX.matcher(input);
        if (!match.matches())
        {
            throw new IllegalArgumentException("Input String was not in a correct format.");
        }
        var major = Integer.parseInt(regexGroup(match, "major"));
        var minor = Integer.parseInt(regexGroup(match, "minor"));
        var patch = Integer.parseInt(regexGroup(match, "patch"));
        var prerelease = match.group("prerelease");
        var metadata = match.group("buildmetadata");
        var ver = new Version(major, minor, patch, prerelease, metadata);
        ver.versionString = input;
        return ver;
    }
    public static Optional<Version> tryParse(String input)
    {
        var match = SEMVER_REGEX.matcher(input);
        if (!match.matches())
        {
            return Optional.empty();
        }
        var major = tryParseInt(regexGroup(match, "major")).orElse(0);
        var minor = tryParseInt(regexGroup(match, "minor")).orElse(0);
        var patch = tryParseInt(regexGroup(match, "patch")).orElse(0);
        var prerelease = match.group("prerelease");
        var metadata = match.group("buildmetadata");
        var ver = new Version(major, minor, patch, prerelease, metadata);
        ver.versionString = input;
        return Optional.of(ver);
    }

    public boolean equals(Object obj)
    {
        if(obj instanceof Version version)
            return equals(version);
        return false;
    }

    public boolean equals(Version other)
    {
        return major == other.major && minor == other.minor && patch == other.patch && Objects.equals(preRelease, other.preRelease) && metadata == other.metadata;
    }

    public boolean majorMinorPatchEquals(Version other)
    {
        return major == other.major && minor == other.minor && patch == other.patch;
    }
    public boolean approximatelyEquals(Version other)
    {
        return major == other.major && minor == other.minor && patch == other.patch && Objects.equals(preRelease, other.preRelease);
    }

    private int compareTo(Version right, boolean ignorePreRelease)
    {
        if (major != right.major)
        {
            return Integer.compare(major, right.major);
        }
        if (minor != right.minor)
        {
            return Integer.compare(minor, right.minor);
        }
        if (patch != right.patch)
        {
            return Integer.compare(patch, right.patch);
        }
        if (!ignorePreRelease && !Objects.equals(preRelease, right.preRelease))
        {
            if (preRelease == null)
            {
                return 1;
            }
            if (right.preRelease == null)
            {
                return -1;
            }
            var leftParts = preRelease.split("\\.");
            var rightParts = right.preRelease.split("\\.");
            for (int i = 0; i < Math.max(leftParts.length, rightParts.length); i++)
            {
                if (i >= leftParts.length)
                {
                    return -1;
                }
                if (i >= rightParts.length)
                {
                    return 1;
                }
                var leftInt = tryParseInt(leftParts[i]);
                var rightInt = tryParseInt(rightParts[i]);
                if (leftInt.isPresent() && rightInt.isPresent())
                {
                    var comparison = Integer.compare(leftInt.get(), rightInt.get());
                    if (comparison != 0)
                    {
                        return comparison;
                    }
                }
                else
                {
                    var comparison = CharSequence.compare(leftParts[i], rightParts[i]);
                    if (comparison != 0)
                    {
                        return comparison;
                    }
                }
            }
        }
        return 0;
    }

    //C# public static boolean operator <(Version left, Version right) => left.CompareTo(right) < 0;
    public static boolean lessThan(Version left, Version right)
    {
        return left.lessThan(right);
    }
    public boolean lessThan(Version right)
    {
        return compareTo(right, false) < 0;
    }
    public boolean lessThan(Version right, boolean ignorePreRelease)
    {
        return compareTo(right, ignorePreRelease) < 0;
    }
    //C# public static boolean operator <=(Version left, Version right) => left.CompareTo(right) <= 0;
    public static boolean lessThanOrEqual(Version left, Version right)
    {
        return left.lessThanOrEqual(right);
    }
    public boolean lessThanOrEqual(Version right)
    {
        return compareTo(right, false) <= 0;
    }
    public boolean lessThanOrEqual(Version right, boolean ignorePreRelease)
    {
        return compareTo(right, ignorePreRelease) <= 0;
    }
    //C# public static boolean operator >(Version left, Version right) => left.CompareTo(right) > 0;
    public static boolean greaterThan(Version left, Version right)
    {
        return left.greaterThan(right);
    }
    public boolean greaterThan(Version right)
    {
        return compareTo(right, false) > 0;
    }
    public boolean greaterThan(Version right, boolean ignorePreRelease)
    {
        return compareTo(right, ignorePreRelease) > 0;
    }
    //C# public static boolean operator >=(Version left, Version right) => left.CompareTo(right) >= 0;
    public static boolean greaterThanOrEqual(Version left, Version right)
    {
        return left.greaterThanOrEqual(right);
    }
    public boolean greaterThanOrEqual(Version right)
    {
        return compareTo(right, false) >= 0;
    }
    public boolean greaterThanOrEqual(Version right, boolean ignorePreRelease)
    {
        return compareTo(right, ignorePreRelease) >= 0;
    }

    public int hashCode()
    {
        return Objects.hash(major, minor, patch, preRelease, metadata);
    }

    public String toString()
    {
        return versionString.isEmpty() ? major + "." + minor + "." + patch + (preRelease != null ? "-" + preRelease : "") + (metadata != null ? "+" + metadata : "") : versionString;
    }

    public static final Version max = new Version(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    public static final Version min = new Version(0, 0, 0);

    public static Optional<Integer> tryParseInt(String input)
    {
        try
        {
            return Optional.of(Integer.parseInt(input));
        } catch (IllegalArgumentException e)
        {
            return Optional.empty();
        }
    }
}
