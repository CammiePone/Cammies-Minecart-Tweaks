package dev.cammiescorner.cammiesminecarttweaks.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {FurnaceMinecartEntity.class, MinecartEntity.class, StorageMinecartEntity.class, CommandBlockMinecartEntity.class}, priority = 0)
public abstract class AllMinecartEntityMixin extends AbstractMinecartEntity {
	protected AllMinecartEntityMixin(EntityType<?> entityType, World world) { super(entityType, world); }

	@Inject(method = "interact", at = @At("HEAD"))
	public void minecarttweaks$heckUMojang(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		super.interact(player, hand);
	}
}
