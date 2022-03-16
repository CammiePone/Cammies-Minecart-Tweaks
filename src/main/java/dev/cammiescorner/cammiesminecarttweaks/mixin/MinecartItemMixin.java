package dev.cammiescorner.cammiesminecarttweaks.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.MinecartItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MinecartItem.class)
public class MinecartItemMixin {
	@ModifyArg(method = "<init>", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/item/Item;<init>(Lnet/minecraft/item/Item$Settings;)V"
	))
	private static Item.Settings minecarttweaks$increaseStackSize(Item.Settings settings) {
		return settings.maxCount(16);
	}
}
