package dev.cammiescorner.cammiesminecarttweaks.mixin;

import dev.cammiescorner.cammiesminecarttweaks.MinecartTweaks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.*;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {ChestMinecartEntity.class, FurnaceMinecartEntity.class, HopperMinecartEntity.class, TntMinecartEntity.class}, priority = 0)
public abstract class BlockMinecartEntityMixin extends AbstractMinecartEntity {
	protected BlockMinecartEntityMixin(EntityType<?> entityType, World world) { super(entityType, world); }

	@Inject(method = "dropItems", at = @At("HEAD"), cancellable = true)
	public void minecarttweaks$dropItems(DamageSource damageSource, CallbackInfo info) {
		if(MinecartTweaks.getConfig().serverTweaks.toolsHarvestBlockMinecarts && damageSource.getAttacker() instanceof PlayerEntity player && player.getMainHandStack().getItem() instanceof ToolItem) {
			remove(Entity.RemovalReason.KILLED);

			if(world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
				if((Object) this instanceof ChestMinecartEntity)
					dropItem(Items.CHEST_MINECART);
				if((Object) this instanceof FurnaceMinecartEntity)
					dropItem(Items.FURNACE_MINECART);
				if((Object) this instanceof HopperMinecartEntity)
					dropItem(Items.HOPPER_MINECART);
				if((Object) this instanceof TntMinecartEntity)
					dropItem(Items.TNT_MINECART);
			}

			info.cancel();
		}
	}
}
