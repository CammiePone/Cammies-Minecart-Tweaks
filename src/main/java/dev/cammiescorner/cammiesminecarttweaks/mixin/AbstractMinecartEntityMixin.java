package dev.cammiescorner.cammiesminecarttweaks.mixin;

import dev.cammiescorner.cammiesminecarttweaks.MinecartTweaks;
import dev.cammiescorner.cammiesminecarttweaks.packets.SyncMinecartPacket;
import dev.cammiescorner.cammiesminecarttweaks.utils.Linkable;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity implements Linkable {
	@Shadow public abstract Direction getMovementDirection();

	@Unique private AbstractMinecartEntity linkedParent;
	@Unique private UUID parentUuid;

	public AbstractMinecartEntityMixin(EntityType<?> type, World world) { super(type, world); }

	@Inject(method = "getMaxOffRailSpeed", at = @At("RETURN"), cancellable = true)
	public void minecarttweaks$increaseSpeed(CallbackInfoReturnable<Double> info) {
		if(getLinkedParent() != null)
			info.setReturnValue(getLinkedParent().getMaxOffRailSpeed());
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void minecarttweaks$tick(CallbackInfo info) {
		if(!world.isClient() && getLinkedParent() != null) {
			PlayerLookup.tracking(this).forEach(player -> SyncMinecartPacket.send(player, linkedParent, (AbstractMinecartEntity) (Object) this));

			double distance = getLinkedParent().distanceTo(this);
			double parentSpeed = getLinkedParent().getVelocity().horizontalLength();

			if(distance <= 5) {
				if(distance > 2) {
					setVelocity(getLinkedParent().getPos().subtract(getPos()).multiply(Math.max(0.1, parentSpeed)));

					if(distance > 2.5)
						setVelocity(getVelocity().multiply(1.1));
				}
				else {
					setVelocity(getVelocity().multiply(0.2));
				}
			}
			else {
				setLinkedParent(null);
				dropStack(new ItemStack(Items.CHAIN));
				return;
			}

			if(getLinkedParent().isRemoved()) {
				setLinkedParent(null);
				dropStack(new ItemStack(Items.CHAIN));
			}
		}
	}

	@Inject(method = "dropItems", at = @At("HEAD"))
	public void minecarttweaks$dropChain(DamageSource damageSource, CallbackInfo info) {
		if(getLinkedParent() != null)
			dropStack(new ItemStack(Items.CHAIN));
	}

	@Inject(method = "collidesWith", at = @At("HEAD"))
	public void minecarttweaks$damageEntities(Entity other, CallbackInfoReturnable<Boolean> info) {
		if(other instanceof AbstractMinecartEntity minecart)
			minecart.setVelocity(getVelocity());

		if(other instanceof LivingEntity living && !living.hasVehicle() && getVelocity().horizontalLength() > 1.5) {
			living.takeKnockback(getVelocity().horizontalLength(), -getVelocity().x, -getVelocity().z);
			living.damage(MinecartTweaks.MINECART, 10);
		}
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
	public void minecarttweaks$readNbt(NbtCompound nbt, CallbackInfo info) {
		if(nbt.contains("ParentUuid"))
			parentUuid = nbt.getUuid("ParentUuid");
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
	public void minecarttweaks$writeNbt(NbtCompound nbt, CallbackInfo info) {
		if(getLinkedParent() != null)
			nbt.putUuid("ParentUuid", getLinkedParent().getUuid());
	}

	@ModifyVariable(method = "moveOnRail", at = @At("STORE"), ordinal = 8)
	private double minecarttweaks$uncapSpeed(double velocityCap) {
		return getVelocity().horizontalLength();
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);

		if(player.isSneaking() && stack.isOf(Items.CHAIN)) {
			if(world instanceof ServerWorld server) {
				NbtCompound nbt = stack.getOrCreateNbt();

				if(nbt.contains("ParentEntity")) {
					if(getUuid() == nbt.getUuid("ParentEntity")) {
						nbt.remove("ParentEntity");
					}
					else {
						if(server.getEntity(nbt.getUuid("ParentEntity")) instanceof AbstractMinecartEntity parent)
							setLinkedParent(parent);

						world.playSound(null, getX(), getY(), getZ(), SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.NEUTRAL, 1F, 1F);

						if(!player.isCreative())
							stack.decrement(1);
						else
							nbt.remove("ParentEntity");
					}
				}
				else {
					nbt.putUuid("ParentEntity", getUuid());
					world.playSound(null, getX(), getY(), getZ(), SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.NEUTRAL, 1F, 1F);
				}
			}

			return ActionResult.success(true);
		}

		return super.interact(player, hand);
	}

	@Override
	public AbstractMinecartEntity getLinkedParent() {
		if(world instanceof ServerWorld server && linkedParent == null && parentUuid != null && server.getEntity(parentUuid) instanceof AbstractMinecartEntity parent)
			setLinkedParent(parent);

		return linkedParent;
	}

	@Override
	public void setLinkedParent(AbstractMinecartEntity parent) {
		linkedParent = parent;

		if(parent == null)
			parentUuid = null;
	}
}
