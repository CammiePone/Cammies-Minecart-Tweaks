package dev.cammiescorner.cammiesminecarttweaks.integration;

import dev.cammiescorner.cammiesminecarttweaks.MinecartTweaks;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = MinecartTweaks.MOD_ID)
public class MinecartTweaksConfig implements ConfigData {
	public double minecartBaseSpeed = 0.5;
	public double furnaceSpeedMultiplier = 2D;
	public float minecartDamage = 20F;
	public boolean canLinkMinecarts = true;

	public double getMinecartBaseSpeed() {
		return Math.max(0.1, minecartBaseSpeed);
	}

	public double getFurnaceSpeedMultiplier() {
		return Math.max(0.1, furnaceSpeedMultiplier);
	}
}
