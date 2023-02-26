package dev.cammiescorner.cammiesminecarttweaks.mixin;

import dev.cammiescorner.cammiesminecarttweaks.utils.Linkable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {FurnaceMinecartEntity.class, MinecartEntity.class, StorageMinecartEntity.class, CommandBlockMinecartEntity.class}, priority = 0)
public abstract class AllMinecartEntityMixin extends AbstractMinecartEntity implements Linkable {
	protected AllMinecartEntityMixin(EntityType<?> entityType, World world) { super(entityType, world); }

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	public void minecarttweaks$heckUMojang(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		if(getMinecartType() == Type.RIDEABLE) {
			AbstractMinecartEntity parent = getLinkedParent();
			AbstractMinecartEntity child = getLinkedChild();
			ItemStack stack = player.getStackInHand(hand);
			Item item = stack.getItem();
			Type type = Type.RIDEABLE;

			if(item == Items.FURNACE)
				type = Type.FURNACE;
			if(item == Items.CHEST)
				type = Type.CHEST;
			if(item == Items.TNT)
				type = Type.TNT;
			if(item == Items.HOPPER)
				type = Type.HOPPER;

			if(type != Type.RIDEABLE) {
				AbstractMinecartEntity minecart = AbstractMinecartEntity.create(world, getX(), getY(), getZ(), type);
				world.spawnEntity(minecart);

				if(parent != null) {
					Linkable.unsetParentChild((Linkable) parent, this);
					Linkable.setParentChild((Linkable) parent, (Linkable) minecart);
				}
				if(child != null) {
					Linkable.unsetParentChild(this, (Linkable) child);
					Linkable.setParentChild((Linkable) minecart, (Linkable) child);
				}

				remove(RemovalReason.DISCARDED);

				if(!player.isCreative())
					stack.decrement(1);

				info.setReturnValue(ActionResult.success(world.isClient()));
			}
		}

		super.interact(player, hand);
	}
}
