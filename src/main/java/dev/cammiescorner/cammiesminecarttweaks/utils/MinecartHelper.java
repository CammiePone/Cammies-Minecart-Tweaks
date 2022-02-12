package dev.cammiescorner.cammiesminecarttweaks.utils;

import dev.cammiescorner.cammiesminecarttweaks.MinecartTweaks;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MinecartHelper {
	public static boolean shouldSlowDown(AbstractMinecartEntity minecart, World world) {
		boolean slowEm = false;
		BlockPos pos = minecart.getBlockPos();

		if(world.getBlockState(pos.down()).isIn(BlockTags.RAILS))
			pos = pos.down();

		BlockState state = world.getBlockState(pos);

		if(state.isIn(BlockTags.RAILS) && state.getBlock() instanceof AbstractRailBlock) {
			Direction horizontal = Direction.getFacing(minecart.getVelocity().getX(), 0, minecart.getVelocity().getZ());
			int distance = (int) Math.ceil(MinecartTweaks.getConfig().getFurnaceSpeedMultiplier() * (MinecartTweaks.getConfig().getMinecartBaseSpeed() * 2));

			for(int h = 0; h < distance; h++) {
				state = world.getBlockState(pos.mutableCopy().offset(horizontal, h));

				if(state.isIn(BlockTags.RAILS) && state.getBlock() instanceof AbstractRailBlock rails) {
					switch(state.get(rails.getShapeProperty())) {
						case NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST -> slowEm = true;
					}
				}
			}

			for(int h = 0; h < distance; h++) {
				state = world.getBlockState(pos.mutableCopy().offset(horizontal.getOpposite(), h));

				if(state.isIn(BlockTags.RAILS) && state.getBlock() instanceof AbstractRailBlock rails) {
					switch(state.get(rails.getShapeProperty())) {
						case NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST -> slowEm = true;
					}
				}
			}
		}

		return slowEm;
	}
}
