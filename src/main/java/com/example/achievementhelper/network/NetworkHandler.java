package com.example.achievementhelper.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import com.example.achievementhelper.AchievementHelperMod;

@EventBusSubscriber(modid = AchievementHelperMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(AchievementHelperMod.MODID);
        registrar.playToClient(
                AchievementDataPacket.ID,
                AchievementDataPacket::new,
                (packet, context) -> context.enqueueWork(() ->
                        AchievementHelperClient.handleAchievementData(packet.achievements())
                )
        );
    }
}