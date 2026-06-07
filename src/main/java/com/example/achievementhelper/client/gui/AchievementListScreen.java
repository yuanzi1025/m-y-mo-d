package com.example.achievementhelper.client.gui;

import com.example.achievementhelper.network.AchievementDataPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.stream.Collectors;

public class AchievementListScreen extends Screen {
    private static final int ITEM_HEIGHT = 30;
    private static final int LEFT_MARGIN = 10;
    private static final int TOP_MARGIN = 40;
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private List<AchievementDataPacket.AchievementData> achievements = new ArrayList<>();
    private Map<String, List<AchievementDataPacket.AchievementData>> groupedByMod = new LinkedHashMap<>();
    private boolean showOnlyUnfinished = false;
    private Button toggleFilterButton;

    public AchievementListScreen() {
        super(Component.literal("成就辅助列表"));
    }

    public void updateAchievements(List<AchievementDataPacket.AchievementData> newAchievements) {
        this.achievements = newAchievements;
        groupedByMod = achievements.stream()
                .collect(Collectors.groupingBy(a -> a.modId, LinkedHashMap::new, Collectors.toList()));
        updateMaxScroll();
    }

    private void updateMaxScroll() {
        int totalItems = getCurrentDisplayList().size();
        maxScroll = Math.max(0, totalItems * ITEM_HEIGHT - (this.height - TOP_MARGIN - 20));
    }

    private List<AchievementDataPacket.AchievementData> getCurrentDisplayList() {
        if (showOnlyUnfinished) {
            return achievements.stream().filter(a -> !a.completed).collect(Collectors.toList());
        } else {
            return achievements;
        }
    }

    @Override
    protected void init() {
        super.init();
        toggleFilterButton = Button.builder(
                        Component.literal(showOnlyUnfinished ? "显示全部" : "只显示未完成"),
                        btn -> {
                            showOnlyUnfinished = !showOnlyUnfinished;
                            btn.setMessage(Component.literal(showOnlyUnfinished ? "显示全部" : "只显示未完成"));
                            scrollOffset = 0;
                            updateMaxScroll();
                        })
                .bounds(width / 2 - 100, 10, 200, 20)
                .build();
        addRenderableWidget(toggleFilterButton);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(font, "成就辅助列表 - " + (showOnlyUnfinished ? "未完成" : "全部"),
                LEFT_MARGIN, 35, 0xFFFFFF);

        List<AchievementDataPacket.AchievementData> list = getCurrentDisplayList();
        int y = TOP_MARGIN - scrollOffset;
        for (AchievementDataPacket.AchievementData info : list) {
            if (y + ITEM_HEIGHT >= TOP_MARGIN && y <= this.height) {
                drawAchievementItem(guiGraphics, info, LEFT_MARGIN, y, this.width - 20, ITEM_HEIGHT);
            }
            y += ITEM_HEIGHT;
        }
    }

    private void drawAchievementItem(GuiGraphics guiGraphics, AchievementDataPacket.AchievementData info, int x, int y, int width, int height) {
        int bgColor = info.completed ? 0x4400AA00 : 0x44AA0000;
        guiGraphics.fill(x, y, x + width, y + height, bgColor);
        guiGraphics.renderOutline(x, y, width, height, 0xFFAAAAAA);
        String modShow = "[" + info.modId + "] ";
        guiGraphics.drawString(font, modShow + info.title,
                x + 5, y + 5, info.completed ? 0x88FF88 : 0xFF8888, false);
        String desc = info.description;
        if (desc.length() > 50) desc = desc.substring(0, 47) + "...";
        guiGraphics.drawString(font, desc, x + 5, y + 18, 0xCCCCCC, false);
        String statusSymbol = info.completed ? "✓" : "✗";
        guiGraphics.drawString(font, statusSymbol, x + width - 15, y + 8, info.completed ? 0x00FF00 : 0xFF0000, true);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int delta = (int) Math.signum(scrollY);
        scrollOffset = Math.min(maxScroll, Math.max(0, scrollOffset - delta * 20));
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}