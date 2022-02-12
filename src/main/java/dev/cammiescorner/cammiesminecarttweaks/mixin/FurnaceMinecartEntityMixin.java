package dev.cammiescorner.cammiesminecarttweaks.mixin;

import dev.cammiescorner.cammiesminecarttweaks.MinecartTweaks;
import dev.cammiescorner.cammiesminecarttweaks.utils.Linkable;
import dev.cammiescorner.cammiesminecarttweaks.utils.MinecartHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(FurnaceMinecartEntity.class)
public abstract class FurnaceMinecartEntityMixin extends AbstractMinecartEntity implements Linkable {
	@Shadow protected abstract boolean isLit();

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
		AtomicBoolean shouldSlowDown = new AtomicBoolean(MinecartHelper.shouldSlowDown(this, world));

		if(getLinkedChild() != null) {
			Linkable linkable = (Linkable) getLinkedChild();
			Set<Linkable> train = new HashSet<>();
			train.add(linkable);

			while((linkable = (Linkable) linkable.getLinkedChild()) instanceof Linkable) {
				train.add(linkable);
			}

			train.forEach(child -> shouldSlowDown.set(shouldSlowDown.get() || MinecartHelper.shouldSlowDown((AbstractMinecartEntity) child, world)));
		}

		if(shouldSlowDown.get() && getVelocity().length() > MinecartTweaks.getConfig().getMinecartBaseSpeed())
			setVelocity(getVelocity().normalize().multiply(MinecartTweaks.getConfig().getMinecartBaseSpeed()));
	}

	@ModifyArgs(method = "tick", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"
	))
	public void minecarttweaks$changeSmokeParticle(Args args) {
		args.set(0, ParticleTypes.CAMPFIRE_COSY_SMOKE);
		args.set(1, getX() + (random.nextFloat() - 0.5));
		args.set(2, getY() + 1);
		args.set(3, getZ() + (random.nextFloat() - 0.5));
		args.set(5, 0.2);
	}

	@ModifyArg(method = "tick", at = @At(value = "INVOKE",
			target = "Ljava/util/Random;nextInt(I)I"
	))
	public int minecarttweaks$removeRandom(int i) {
		return 1;
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/vehicle/FurnaceMinecartEntity;isLit()Z"
	))
	public boolean minecarttweaks$particleFrequency(FurnaceMinecartEntity minecart) {
		return isLit();
	}
}
