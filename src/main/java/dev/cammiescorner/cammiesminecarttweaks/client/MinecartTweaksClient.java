package dev.cammiescorner.cammiesminecarttweaks.client;

import dev.cammiescorner.cammiesminecarttweaks.MinecartTweaks;
import dev.cammiescorner.cammiesminecarttweaks.common.packets.SyncChainedMinecartPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

@Environment(EnvType.CLIENT)
public class MinecartTweaksClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(SyncChainedMinecartPacket.ID, SyncChainedMinecartPacket::handle);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), MinecartTweaks.CROSSED_RAIL);

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			ItemStack stack = player.getStackInHand(hand);

			if(world.isClient() && entity instanceof FurnaceMinecartEntity && MinecartTweaks.getConfig().clientTweaks.dontEatEnchantedItems && stack.hasEnchantments())
				return ActionResult.CONSUME;

			return ActionResult.PASS;
		});
	}
}
