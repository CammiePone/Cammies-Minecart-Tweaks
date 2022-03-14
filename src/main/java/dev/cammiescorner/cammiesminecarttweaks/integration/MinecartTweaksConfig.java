package dev.cammiescorner.cammiesminecarttweaks.integration;

import dev.cammiescorner.cammiesminecarttweaks.MinecartTweaks;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;

@Config(name = MinecartTweaks.MOD_ID)
public class MinecartTweaksConfig implements ConfigData {
	@CollapsibleObject public ClientTweaks clientTweaks = new ClientTweaks();
	@CollapsibleObject public ServerTweaks serverTweaks = new ServerTweaks();

	public double getFurnaceMinecartSpeed() {
		return Math.max(0.1, serverTweaks.furnaceMinecartSpeed * 0.05);
	}

	public double getOtherMinecartSpeed() {
		return Math.max(0.1, serverTweaks.otherMinecartSpeed * 0.05);
	}

	public double getMaxSpeedAroundTurns() {
		return Math.min(1, serverTweaks.maxSpeedAroundTurns * 0.05);
	}

	public static class ClientTweaks {
		public boolean useCampfireSmoke = true;
		public boolean dontEatEnchantedItems = true;
		public boolean playerViewIsLocked = false;
		@BoundedDiscrete(max = 90L) public int maxViewAngle = 90;
	}

	public static class ServerTweaks {
		public double furnaceMinecartSpeed = 40D;
		public double otherMinecartSpeed = 10D;
		public double maxSpeedAroundTurns = 8D;
		public float minecartDamage = 20F;
		public int furnaceMaxBurnTime = 72000;
		public boolean canLinkMinecarts = true;
		public boolean shouldPoweredRailsStopFurnace = true;
		public boolean furnacesCanUseAllFuels = true;
		public boolean furnaceMinecartsLoadChunks = false;
		public boolean minecartsCanSwitchRails = true;
		public boolean toolsHarvestBlockMinecarts = true;
	}
}
