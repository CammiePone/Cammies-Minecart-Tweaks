package dev.cammiescorner.cammiesminecarttweaks;

import dev.cammiescorner.cammiesminecarttweaks.integration.MinecartTweaksConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.util.Identifier;

public class MinecartTweaks implements ModInitializer {
	public static ConfigHolder<MinecartTweaksConfig> configHolder;
	public static final String MOD_ID = "minecarttweaks";

	@Override
	public void onInitialize() {
		AutoConfig.register(MinecartTweaksConfig.class, JanksonConfigSerializer::new);
		configHolder = AutoConfig.getConfigHolder(MinecartTweaksConfig.class);
	}

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}

	public static MinecartTweaksConfig getConfig() {
		return configHolder.getConfig();
	}

	public static DamageSource minecart(Entity entity) {
		return new EntityDamageSource(MOD_ID + ".minecart", entity).setBypassesArmor().setUnblockable();
	}
}
