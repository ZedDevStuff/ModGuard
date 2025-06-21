package dev.zeddevstuff.modguard;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ModGuardAgent
{
    private static final Gson GSON = new Gson();
    private final File worldDir;
    public File getWorldDir() { return worldDir; }
    public boolean isValid() { return worldDir.isDirectory() && modGuardFile.exists(); }
    private final File modGuardFile;
    private List<LoaderUtils.ModEntry> savedModlist = List.of();
    private ModGuardReport report;

    public ModGuardAgent(String worldDir)
    {
        this(worldDir == null ? null : new File(worldDir));
    }
    public ModGuardAgent(File worldDir)
    {
        this.worldDir = worldDir;
        this.modGuardFile = new File(worldDir, "modguard");
        if(isValid())
        {
            try {
                String raw = FileUtils.readFileToString(modGuardFile, "UTF-8")
                    .replace("\n", "")
                    .replace("\r", "");
                if(!raw.isBlank()) savedModlist = Arrays.stream(raw.split(";"))
                        .map(line -> {
                            String[] parts = line.split(":");
                            if(parts.length == 1) return new LoaderUtils.ModEntry(parts[0], "0.0.0");
                            else if(parts.length == 2) return new LoaderUtils.ModEntry(parts[0], parts[1]);
                            else throw new IllegalArgumentException("Invalid mod entry format: " + line);
                        })
                        .toList();
            } catch (Exception ignored) {

            }

        }
    }

    public ModGuardReport generateReport()
    {
        if(!isValid())
        {
            report = ModGuardReport.pass();
            return report;
        }
        if(report != null) return report;
        // TODO: Use the configuration to determine how to compare the mod lists. In the meantime we will just return a can't proceed report if anything changed at all.
        List<LoaderUtils.ModEntry> currentModlist = LoaderUtils.getMods();
        Diff<LoaderUtils.ModEntry> diff = Diff.modlistDiff(savedModlist, currentModlist);
        diff.getAdded().removeIf(mod -> !ModGuard.getConfig().notifyModAdditions);
        diff.getModified().removeIf(mod -> {
            Optional<Version> oldVersion = Version.tryParse(savedModlist.stream()
                    .filter(entry -> entry.id().equals(mod.id()))
                    .findFirst()
                    .map(LoaderUtils.ModEntry::version)
                    .orElse("0.0.0"));
            Optional<Version> newVersion = Version.tryParse(mod.version());
            if(oldVersion.isEmpty() || newVersion.isEmpty()) return true;
            if(ModGuard.getConfig().changeType == ModGuardConfig.ChangeType.MAJOR_ONLY)
            {
                return oldVersion.get().getMajor() == newVersion.get().getMajor();
            }
            else if(ModGuard.getConfig().changeType == ModGuardConfig.ChangeType.MINOR_OR_GREATER)
            {
                return oldVersion.get().getMajor() == newVersion.get().getMajor() &&
                        oldVersion.get().getMinor() == newVersion.get().getMinor();
            }
        else if(ModGuard.getConfig().changeType == ModGuardConfig.ChangeType.PATCH_OR_GREATER)
            {
                return oldVersion.get().getMajor() == newVersion.get().getMajor() &&
                        oldVersion.get().getMinor() == newVersion.get().getMinor() &&
                        oldVersion.get().getPatch() == newVersion.get().getPatch();
            }
            return false;
        });
        diff.getRemoved().removeIf(mod -> !ModGuard.getConfig().notifyModRemovals);
        boolean canProceed = diff.getAdded().isEmpty() && diff.getRemoved().isEmpty() && diff.getModified().isEmpty();
        report = new ModGuardReport(diff, canProceed);
        return report;
    }

    public void saveModlist()
    {
        if(!worldDir.isDirectory()) return;
        try {
            String modlist = savedModlist.stream()
                    .map(mod -> mod.id() + ":" + mod.version())
                    .reduce((a, b) -> a + ";\n" + b)
                    .orElse("");
            FileUtils.writeStringToFile(modGuardFile, modlist, "UTF-8");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void ensureCreated()
    {
        if(!worldDir.isDirectory()) return;
        if(!modGuardFile.exists())
        {
            savedModlist = LoaderUtils.getMods();
            String modlist = savedModlist.stream()
                    .map(mod -> mod.id() + ":" + mod.version())
                    .reduce((a, b) -> a + ";\n" + b)
                    .orElse("");
            try {
                FileUtils.writeStringToFile(modGuardFile, modlist, "UTF-8");
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    public void overwriteFile()
    {
        if(!worldDir.isDirectory()) return;
        savedModlist = LoaderUtils.getMods();
        String modlist = savedModlist.stream()
            .map(mod -> mod.id() + ":" + mod.version())
            .reduce((a, b) -> a + ";\n" + b)
            .orElse("");
        try {
            FileUtils.writeStringToFile(modGuardFile, modlist, "UTF-8");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static class ModGuardReport
    {
        public final Diff<LoaderUtils.ModEntry> diff;
        public final boolean canProceed;
        public ModGuardReport(Diff<LoaderUtils.ModEntry> diff, boolean canProceed)
        {
            this.diff = diff;
            this.canProceed = canProceed;
        }

        public static ModGuardReport pass()
        {
            return new ModGuardReport(new Diff<>(List.of(), List.of()), true);
        }
    }
}
