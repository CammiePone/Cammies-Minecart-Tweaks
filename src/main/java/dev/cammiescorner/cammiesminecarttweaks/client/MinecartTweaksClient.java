package dev.cammiescorner.cammiesminecarttweaks.client;

import dev.cammiescorner.cammiesminecarttweaks.packets.SyncChainedMinecartPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class MinecartTweaksClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(SyncChainedMinecartPacket.ID, SyncChainedMinecartPacket::handle);
	}
}
