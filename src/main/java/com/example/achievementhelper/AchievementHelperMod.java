package com.example.achievementhelper;

import com.example.achievementhelper.network.AchievementDataPacket;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;
import java.util.List;

@Mod(AchievementHelperMod.MODID)
public class AchievementHelperMod {
    public static final String MODID = "achievementhelper";

    public AchievementHelperMod(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("achievementhelper")
                .executes(ctx -> {
                    if (ctx.getSource().getEntity() instanceof ServerPlayer player) {
                        sendAchievementDataToClient(player);
                    } else {
                        ctx.getSource().sendFailure(Component.literal("该命令只能由玩家执行"));
                    }
                    return 1;
                })
        );
    }

    private void sendAchievementDataToClient(ServerPlayer player) {
        var advancements = player.server.getAdvancements().getAllAdvancements();
        var progress = player.getAdvancements();
        List<AchievementDataPacket.AchievementData> dataList = new ArrayList<>();

        for (var holder : advancements) {
            if (holder.value().display().isPresent()) {
                boolean done = progress.getOrStartProgress(holder).isDone();
                String modId = holder.id().getNamespace();
                String title = holder.value().display().get().getTitle().getString();
                String description = holder.value().display().get().getDescription().getString();
                dataList.add(new AchievementDataPacket.AchievementData(modId, title, description, done));
            }
        }
        var packet = new AchievementDataPacket(dataList);
        player.connection.connection.channel().writeAndFlush(packet);
    }
}