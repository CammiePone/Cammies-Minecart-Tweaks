package dev.cammiescorner.cammiesminecarttweaks.common.packets;

import dev.cammiescorner.cammiesminecarttweaks.MinecartTweaks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SyncMinecraftPosPacket {
	public static final Identifier ID = MinecartTweaks.id("sync_minecart_pos");

	public static void send(PlayerEntity player, AbstractMinecartEntity minecart) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeInt(minecart.getId());
		buf.writeDoubleLE(minecart.getX());
		buf.writeDoubleLE(minecart.getY());
		buf.writeDoubleLE(minecart.getZ());
		buf.writeDoubleLE(minecart.getVelocity().getX());
		buf.writeDoubleLE(minecart.getVelocity().getY());
		buf.writeDoubleLE(minecart.getVelocity().getZ());
		buf.writeFloatLE(minecart.getYaw());
		buf.writeFloatLE(minecart.getPitch());
		ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, ID, buf);
	}

	@Environment(EnvType.CLIENT)
	public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
		int minecartId = buf.readInt();
		double posX = buf.readDoubleLE();
		double posY = buf.readDoubleLE();
		double posZ = buf.readDoubleLE();
		double velX = buf.readDoubleLE();
		double velY = buf.readDoubleLE();
		double velZ = buf.readDoubleLE();
		float yaw = buf.readFloatLE();
		float pitch = buf.readFloatLE();

		client.submit(() -> {
			if(client.world != null) {
				World world = client.world;
			}
		});
	}
}
