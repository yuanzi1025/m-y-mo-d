package com.example.achievementhelper.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import com.example.achievementhelper.AchievementHelperMod;

import java.util.List;

public record AchievementDataPacket(List<AchievementData> achievements) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(AchievementHelperMod.MODID, "achievement_data");

    public AchievementDataPacket(FriendlyByteBuf buf) {
        this(buf.readList(AchievementData::new));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(achievements, (b, a) -> a.write(b));
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static class AchievementData {
        public final String modId;
        public final String title;
        public final String description;
        public final boolean completed;

        public AchievementData(String modId, String title, String description, boolean completed) {
            this.modId = modId;
            this.title = title;
            this.description = description;
            this.completed = completed;
        }

        public AchievementData(FriendlyByteBuf buf) {
            this(buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readBoolean());
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeUtf(modId);
            buf.writeUtf(title);
            buf.writeUtf(description);
            buf.writeBoolean(completed);
        }
    }
}