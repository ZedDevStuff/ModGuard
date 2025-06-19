package dev.zeddevstuff.modguard.mixin;

import dev.zeddevstuff.modguard.ModGuardAgent;
import dev.zeddevstuff.modguard.client.gui.screens.ModGuardModal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(WorldSelectionList.WorldListEntry.class)
public abstract class WorldListEntryMixin
{
    @Unique
    private ModGuardAgent modguard$agent = null;
    @Shadow @Final
    LevelSummary summary;

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "joinWorld", at = @At(value = "HEAD"), cancellable = true)
    public void joinWorld(CallbackInfo ci)
    {
        if(this.modguard$agent == null)
            this.modguard$agent = new ModGuardAgent(new File(Minecraft.getInstance().gameDirectory, "saves/" + summary.getLevelId()));
        modguard$agent.ensureCreated();
        var report = modguard$agent.generateReport();
        if (!report.canProceed)
        {
            minecraft.setScreen(ModGuardModal.create(modguard$agent, (WorldSelectionList.WorldListEntry)(Object)this));
            ci.cancel();
        }
        else
        {
            modguard$agent.overwriteFile();
        }
    }
}
