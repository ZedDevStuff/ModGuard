package dev.zeddevstuff.modguard.client.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/// Adapted from NeoForge's InfoPanel, licensed under LGPL-2.1
public class ScrollableTextPanel extends ScrollPanel {
    private Font font;
    private List<FormattedCharSequence> lines = Collections.emptyList();
    private int padding = 0;
    public void setPadding(int padding)
    {
        this.padding = padding;
    }

    public ScrollableTextPanel(Minecraft mcIn, int width, int height, int x, int y)
    {
        super(mcIn, width, height, y, x);
        font = mcIn.font;
    }

    public void setLines(List<String> lines)
    {
        this.lines = resizeContent(lines);
    }
    public void setLinesComp(List<Component> lines)
    {
        this.lines = resizeContentComp(lines);
    }

    public void clearLines()
    {
        this.lines = Collections.emptyList();
    }

    public void setPosAndSize(int x, int y, int width, int height)
    {
        this.width = width;
        this.height = height;
        this.top = y;
        this.left = x;
        this.bottom = y + this.height;
        this.right = x + this.width;
        this.barLeft = this.left + this.width - this.barWidth;
    }

    private List<FormattedCharSequence> resizeContent(List<String> lines)
    {
        List<FormattedCharSequence> ret = new ArrayList<>();
        for (String line : lines)
        {
            if (line == null)
            {
                ret.add(null);
                continue;
            }
            // TODO: Figure out how to deserialise the Component to preserve formatting
            Component chat = Component.literal(line);
            int maxTextLength = this.width - 12;
            if (maxTextLength >= 0)
            {
                ret.addAll(Language.getInstance().getVisualOrder(font.getSplitter().splitLines(chat, maxTextLength, Style.EMPTY)));
            }
        }
        return ret;
    }
    private List<FormattedCharSequence> resizeContentComp(List<Component> lines)
    {
        List<FormattedCharSequence> ret = new ArrayList<>();
        for (Component line : lines)
        {
            if (line == null)
            {
                ret.add(null);
                continue;
            }
            int maxTextLength = this.width - 12;
            if (maxTextLength >= 0)
            {
                ret.addAll(Language.getInstance().getVisualOrder(font.getSplitter().splitLines(line, maxTextLength, Style.EMPTY)));
            }
        }
        return ret;
    }

    @Override
    public int getContentHeight()
    {
        int height = 50;
        height += (lines.size() * font.lineHeight);
        if (height < this.bottom - this.top - 8)
            height = this.bottom - this.top - 8;
        return height;
    }

    @Override
    protected int getScrollAmount()
    {
        return font.lineHeight * 3;
    }

    @Override
    protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, int mouseX, int mouseY)
    {
        for (FormattedCharSequence line : lines)
        {
            if (line != null)
            {
                guiGraphics.drawString(font, line, left + padding , relativeY, 0xFFFFFFFF);
            }
            relativeY += font.lineHeight;
        }

        final Style component = findTextLine(mouseX, mouseY);
        if (component != null)
        {
            guiGraphics.renderComponentHoverEffect(font, component, mouseX, mouseY);
        }
    }

    private Style findTextLine(final int mouseX, final int mouseY)
    {
        if (!isMouseOver(mouseX, mouseY))
            return null;

        double offset = (mouseY - top - border) + scrollDistance;
        if (offset <= 0)
            return null;

        int lineIdx = (int) (offset / font.lineHeight);
        if (lineIdx >= lines.size() || lineIdx < 0)
            return null;

        FormattedCharSequence line = lines.get(lineIdx);
        if (line != null)
        {
            return font.getSplitter().componentStyleAtWidth(line, mouseX - left - border - 1);
        }
        return null;
    }

    @Override
    public NarrationPriority narrationPriority()
    {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {}
}