package dev.cammiescorner.cammiesminecarttweaks.mixin;

import dev.cammiescorner.cammiesminecarttweaks.MinecartTweaks;
import dev.cammiescorner.cammiesminecarttweaks.packets.SyncChainedMinecartPacket;
import dev.cammiescorner.cammiesminecarttweaks.utils.Linkable;
import dev.cammiescorner.cammiesminecarttweaks.utils.MinecartHelper;
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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;
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
		else
			info.setReturnValue(MinecartTweaks.getConfig().getMinecartBaseSpeed());
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void minecarttweaks$tick(CallbackInfo info) {
		if(!world.isClient()) {
			PlayerLookup.tracking(this).forEach(player -> SyncChainedMinecartPacket.send(player, linkedParent, (AbstractMinecartEntity) (Object) this));

			if(getLinkedParent() != null) {
				double distance = getLinkedParent().distanceTo(this) - 1;

				if(distance <= 5) {
					Vec3d direction = getLinkedParent().getPos().subtract(getPos()).normalize();

					if(distance > 1) {
						Vec3d parentVelocity = getLinkedParent().getVelocity();

						if(getVelocity().horizontalLength() == 0)
							setVelocity(direction.multiply(parentVelocity.length()));
						else
							setVelocity(getVelocity().normalize().multiply(parentVelocity.length()));

						setVelocity(direction.multiply(distance));
					}
					else if(distance < 1)
						setVelocity(direction.multiply(1D / -Math.max(0.001, distance)));
					else
						setVelocity(Vec3d.ZERO);
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
			else if(MinecartHelper.shouldSlowDown((AbstractMinecartEntity) (Object) this, world) && getVelocity().horizontalLength() > MinecartTweaks.getConfig().getMinecartBaseSpeed())
				setVelocity(getVelocity().normalize().multiply(MinecartTweaks.getConfig().getMinecartBaseSpeed()));
		}
	}

	@Inject(method = "dropItems", at = @At("HEAD"))
	public void minecarttweaks$dropChain(DamageSource damageSource, CallbackInfo info) {
		if(getLinkedParent() != null)
			dropStack(new ItemStack(Items.CHAIN));
	}

	@Inject(method = "collidesWith", at = @At("HEAD"), cancellable = true)
	public void minecarttweaks$damageEntities(Entity other, CallbackInfoReturnable<Boolean> info) {
		if(other instanceof AbstractMinecartEntity minecart)
			minecart.setVelocity(getVelocity());

		float damage = MinecartTweaks.getConfig().minecartDamage;

		if(damage > 0 && !world.isClient() && other instanceof LivingEntity living && living.isAlive() && !living.hasVehicle() && getVelocity().horizontalLength() > 1.5) {
			living.takeKnockback(getVelocity().horizontalLength(), -getVelocity().x, -getVelocity().z);
			living.damage(MinecartTweaks.minecart(this), damage);
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

	@Redirect(method = "moveOnRail", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(DD)D"))
	private double minecarttweaks$uncapSpeed(double garbo, double uncappedSpeed) {
		return uncappedSpeed;
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		if(MinecartTweaks.getConfig().canLinkMinecarts) {
			ItemStack stack = player.getStackInHand(hand);

			if(player.isSneaking() && stack.isOf(Items.CHAIN)) {
				if(world instanceof ServerWorld server) {
					NbtCompound nbt = stack.getOrCreateNbt();

					if(nbt.contains("ParentEntity")) {
						if(getUuid().equals(nbt.getUuid("ParentEntity"))) {
							nbt.remove("ParentEntity");
							stack.setNbt(null);
						}
						else {
							if(server.getEntity(nbt.getUuid("ParentEntity")) instanceof AbstractMinecartEntity parent) {
								Linkable linkable = (Linkable) parent;
								Set<Linkable> train = new HashSet<>();
								train.add(linkable);

								while((linkable = (Linkable) linkable.getLinkedParent()) instanceof Linkable) {
									train.add(linkable);
								}

								if(train.contains(this))
									player.sendMessage(new TranslatableText(MinecartTweaks.MOD_ID + ".cant_link_to_engine").formatted(Formatting.RED), true);
								else
									setLinkedParent(parent);
							}

							world.playSound(null, getX(), getY(), getZ(), SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.NEUTRAL, 1F, 1F);

							if(!player.isCreative())
								stack.decrement(1);

							nbt.remove("ParentEntity");

							if(nbt.isEmpty())
								stack.setNbt(null);
						}
					}
					else {
						nbt.putUuid("ParentEntity", getUuid());
						world.playSound(null, getX(), getY(), getZ(), SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.NEUTRAL, 1F, 1F);
					}
				}

				return ActionResult.success(true);
			}
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
