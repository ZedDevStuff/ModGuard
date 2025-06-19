package dev.zeddevstuff.modguard.mixin;

import dev.zeddevstuff.modguard.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin
{
    @Shadow @Final
    WorldCreationUiState uiState;

    @Inject(method = "removeTempDataPackDir", at = @At(value = "HEAD"))
    public void idk(CallbackInfo ci)
    {
        File worldDir = new File(Minecraft.getInstance().gameDirectory, "saves/" + this.uiState.getTargetFolder());
        if(worldDir.isDirectory())
        {
            try
            {
                FileUtils.writeStringToFile(new File(worldDir, "modguard"), Utils.buildModGuardList(), "UTF-8");
            } catch (Exception ignored) {}
        }
    }
}
