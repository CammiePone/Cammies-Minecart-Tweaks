package dev.cammiescorner.cammiesminecarttweaks;

import dev.cammiescorner.cammiesminecarttweaks.common.blocks.CrossedRailBlock;
import dev.cammiescorner.cammiesminecarttweaks.integration.MinecartTweaksConfig;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MinecartTweaks implements ModInitializer {
	public static final String MOD_ID = "minecarttweaks";
	public static final Block CROSSED_RAIL = new CrossedRailBlock();

	@Override
	public void onInitialize() {
		MidnightConfig.init(MinecartTweaks.MOD_ID, MinecartTweaksConfig.class);

		Registry.register(Registry.BLOCK, id("crossed_rail"), CROSSED_RAIL);
		Registry.register(Registry.ITEM, id("crossed_rail"), new BlockItem(CROSSED_RAIL, new Item.Settings().group(ItemGroup.TRANSPORTATION)));
	}

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}

	public static DamageSource minecart(Entity entity) {
		return new EntityDamageSource(MOD_ID + ".minecart", entity);
	}
}
