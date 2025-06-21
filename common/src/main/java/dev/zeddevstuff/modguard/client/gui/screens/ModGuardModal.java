package dev.zeddevstuff.modguard.client.gui.screens;

import dev.zeddevstuff.modguard.ModGuardAgent;
import dev.zeddevstuff.modguard.Utils;
import dev.zeddevstuff.modguard.client.gui.widgets.ScrollableTextPanel;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModGuardModal extends Screen
{
    private final Screen parentScreen;
    private final StringWidget titleWidget;
    private final ScrollableTextPanel contentWidget;
    private final Button cancelButton, proceedButton, backupButton;
    private WorldSelectionList.WorldListEntry worldListEntry;
    private ModGuardAgent agent;

    protected ModGuardModal(Component title, String content)
    {
        super(title);
        this.parentScreen = Minecraft.getInstance().screen;
        this.titleWidget = new StringWidget(title, Minecraft.getInstance().font);
        titleWidget.alignCenter();
        this.contentWidget = new ScrollableTextPanel(Minecraft.getInstance(), 300, 200, 0, 60);
        contentWidget.setLines(Arrays.stream(content.split("\n")).toList());
        this.cancelButton = Button.builder(Component.translatable("gui.cancel"), this::cancelClicked)
            .size(100, 20)
            .build();
        this.proceedButton = Button.builder(Component.translatable("selectWorld.versionJoinButton"), this::proceedClicked)
            .size(100, 20)
            .build();
        this.backupButton = Button.builder(Component.translatable("selectWorld.edit.backup"), this::backupClicked)
            .size(100, 20)
            .build();

    }
    protected ModGuardModal(Component title, List<Component> content)
    {
        super(title);
        this.parentScreen = Minecraft.getInstance().screen;
        this.titleWidget = new StringWidget(title, Minecraft.getInstance().font);
        titleWidget.alignCenter();
        this.contentWidget = new ScrollableTextPanel(Minecraft.getInstance(), 300, 200, 0, 60);
        contentWidget.setLinesComp(content);
        this.cancelButton = Button.builder(Component.translatable("gui.cancel"), this::cancelClicked)
            .size(100, 20)
            .build();
        this.proceedButton = Button.builder(Component.translatable("selectWorld.versionJoinButton"), this::proceedClicked)
            .size(100, 20)
            .build();
        this.backupButton = Button.builder(Component.translatable("selectWorld.edit.backup"), this::backupClicked)
            .size(100, 20)
            .build();

    }

    @Override
    protected void init()
    {
        addRenderableOnly(titleWidget);
        addRenderableWidget(contentWidget);
        addRenderableWidget(cancelButton);
        addRenderableWidget(proceedButton);
        addRenderableWidget(backupButton);
        rebuildWidgets();
    }

    @Override
    protected void rebuildWidgets()
    {
        titleWidget.setX(width / 2 - titleWidget.getWidth() / 2);
        titleWidget.setY(20);
        contentWidget.setPosAndSize(20, 60, width - 40, height - 120);
        cancelButton.setX((width - cancelButton.getWidth()) / 2 - 110);
        cancelButton.setY(height - 40);
        proceedButton.setX((width - proceedButton.getWidth()) / 2 + 110);
        proceedButton.setY(height - 40);
        backupButton.setX((width - backupButton.getWidth()) / 2 );
        backupButton.setY(height - 40);
    }

    private void cancelClicked(Button button)
    {
        assert minecraft != null;
        minecraft.setScreen(parentScreen);
    }
    private void proceedClicked(Button button)
    {
        agent.overwriteFile();
        worldListEntry.joinWorld();
    }
    private void backupClicked(Button button)
    {
        Utils.backupWorld(agent.getWorldDir().getAbsolutePath());
        agent.overwriteFile();
        worldListEntry.joinWorld();
    }

    public static ModGuardModal create(ModGuardAgent agent, WorldSelectionList.WorldListEntry worldListEntry)
    {
        List<Component> content = new ArrayList<>();
        var report = agent.generateReport();
        for(var mod : report.diff.getAdded())
        {
            content.add(Component.translatable("generic.modguard.added", mod.id() + ":" + mod.version())
                .withStyle(ChatFormatting.GREEN));
        }
        for (var mod : report.diff.getRemoved())
        {
            content.add(Component.translatable("generic.modguard.removed", mod.id() + ":" + mod.version())
                .withStyle(ChatFormatting.RED));
        }
        for(var mod : report.diff.getModified())
        {
            content.add(Component.translatable("generic.modguard.modified", mod.id() + " - " + report.diff.getModifiedOld().get(report.diff.getModified().indexOf(mod)).version() + " -> " + mod.version())
                .withStyle(ChatFormatting.AQUA));
        }
        var modal = new ModGuardModal(
            Component.translatable("prompt.modguard.modlist_changed"),
            content);
        modal.worldListEntry = worldListEntry;
        modal.agent = agent;
        return modal;
    }
}
