package dev.cammiescorner.cammiesminecarttweaks;

import dev.cammiescorner.cammiesminecarttweaks.common.blocks.CrossedRailBlock;
import dev.cammiescorner.cammiesminecarttweaks.integration.MinecartTweaksConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MinecartTweaks implements ModInitializer {
	public static ConfigHolder<MinecartTweaksConfig> configHolder;
	public static final String MOD_ID = "minecarttweaks";
	public static final Block CROSSED_RAIL = new CrossedRailBlock();

	@Override
	public void onInitialize() {
		AutoConfig.register(MinecartTweaksConfig.class, JanksonConfigSerializer::new);
		configHolder = AutoConfig.getConfigHolder(MinecartTweaksConfig.class);

		Registry.register(Registry.BLOCK, id("crossed_rail"), CROSSED_RAIL);
		Registry.register(Registry.ITEM, id("crossed_rail"), new BlockItem(CROSSED_RAIL, new Item.Settings().group(ItemGroup.TRANSPORTATION)));
	}

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}

	public static MinecartTweaksConfig getConfig() {
		return configHolder.getConfig();
	}

	public static DamageSource minecart(Entity entity) {
		return new EntityDamageSource(MOD_ID + ".minecart", entity);
	}
}
