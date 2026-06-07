package com.example.achievementhelper.client;

import com.example.achievementhelper.AchievementHelperMod;
import com.example.achievementhelper.client.gui.AchievementListScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = AchievementHelperMod.MODID, value = Dist.CLIENT)
public class AchievementHelperClient {
    public static final KeyMapping OPEN_GUI_KEY = new KeyMapping(
            "key.achievementhelper.open_gui",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.categories.achievementhelper"
    );

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_GUI_KEY);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null && OPEN_GUI_KEY.consumeClick()) {
            mc.setScreen(new AchievementListScreen());
        }
    }
}