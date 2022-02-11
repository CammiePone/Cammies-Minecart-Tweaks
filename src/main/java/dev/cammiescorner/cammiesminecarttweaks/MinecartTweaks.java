package dev.cammiescorner.cammiesminecarttweaks;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Identifier;

public class MinecartTweaks implements ModInitializer {
	public static final String MOD_ID = "minecarttweaks";
	public static final DamageSource MINECART = new DamageSource(MOD_ID + ".minecart").setBypassesArmor().setUnblockable();

	@Override
	public void onInitialize() {

	}

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}
}
