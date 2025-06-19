package dev.zeddevstuff.modguard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Utils
{
    public static void backupWorld(String worldPath)
    {
        File worldDir = new File(worldPath);
        if (!worldDir.exists() || !worldDir.isDirectory()) {
            System.err.println("World directory does not exist: " + worldPath);
        }
        else
        {
            var currentTime = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm").format(LocalDateTime.now());
            File backupFile = new File(worldDir + "_backup_" + currentTime + ".zip");
            try(FileOutputStream fos = new FileOutputStream(backupFile);
                ZipOutputStream zos = new ZipOutputStream(fos))
            {
                zipFile(worldDir, worldDir.getName(), zos);
            }
            catch (Exception e)
            {
                System.err.println("Failed to create backup zip file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException
    {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    public static String buildModGuardList()
    {
        return LoaderUtils.getMods().stream()
            .map(mod -> mod.id() + ":" + mod.version())
            .reduce((a, b) -> a + ";\n" + b)
            .orElse("");
    }
}
