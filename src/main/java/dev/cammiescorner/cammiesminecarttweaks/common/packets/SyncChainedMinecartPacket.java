package dev.cammiescorner.cammiesminecarttweaks.common.packets;

import dev.cammiescorner.cammiesminecarttweaks.MinecartTweaks;
import dev.cammiescorner.cammiesminecarttweaks.utils.Linkable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SyncChainedMinecartPacket {
	public static final Identifier ID = MinecartTweaks.id("sync_chained_minecart");

	public static void send(ServerPlayerEntity player, AbstractMinecartEntity parent, AbstractMinecartEntity child) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeBoolean(parent != null);

		if(parent != null)
			buf.writeInt(parent.getId());

		buf.writeInt(child.getId());
		ServerPlayNetworking.send(player, ID, buf);
	}

	@Environment(EnvType.CLIENT)
	public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
		boolean parentExists = buf.readBoolean();
		int parentId = -1;

		if(parentExists)
			parentId = buf.readInt();

		int childId = buf.readInt();

		int finalParentId = parentId;
		client.submit(() -> {
			if(client.world != null) {
				ClientWorld world = client.world;

				if(world.getEntityById(childId) instanceof Linkable linkable) {
					if(parentExists && world.getEntityById(finalParentId) instanceof AbstractMinecartEntity parent)
						linkable.setLinkedParent(parent);
					else
						linkable.setLinkedParent(null);
				}
			}
		});
	}
}
