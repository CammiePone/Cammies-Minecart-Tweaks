package dev.cammiescorner.cammiesminecarttweaks.mixin;

import dev.cammiescorner.cammiesminecarttweaks.MinecartTweaks;
import dev.cammiescorner.cammiesminecarttweaks.utils.MinecartHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FurnaceMinecartEntity.class)
public abstract class FurnaceMinecartEntityMixin extends AbstractMinecartEntity {
	@Shadow protected abstract boolean isLit();
	@Shadow private int fuel;

	protected FurnaceMinecartEntityMixin(EntityType<?> entityType, World world) { super(entityType, world); }

	@Inject(method = "getMaxOffRailSpeed", at = @At("RETURN"), cancellable = true)
	public void minecarttweaks$increaseSpeed(CallbackInfoReturnable<Double> info) {
		if(isLit())
			info.setReturnValue(super.getMaxOffRailSpeed() * MinecartTweaks.getConfig().getFurnaceSpeedMultiplier());
		else
			info.setReturnValue(super.getMaxOffRailSpeed());
	}

	@Inject(method = "moveOnRail", at = @At("TAIL"))
	public void minecarttweaks$slowDown(BlockPos pos, BlockState state, CallbackInfo info) {
		if(MinecartTweaks.getConfig().shouldPoweredRailsStopFurnace && state.isOf(Blocks.POWERED_RAIL) && !state.get(PoweredRailBlock.POWERED))
			fuel = 0;

		if(MinecartHelper.shouldSlowDown(this, world) && getVelocity().horizontalLength() > MinecartTweaks.getConfig().getMinecartBaseSpeed())
			setVelocity(getVelocity().normalize().multiply(MinecartTweaks.getConfig().getMinecartBaseSpeed() - 0.1));
	}
}
