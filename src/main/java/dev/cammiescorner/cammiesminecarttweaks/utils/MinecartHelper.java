package dev.cammiescorner.cammiesminecarttweaks.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class MinecartHelper {
	public static boolean shouldSlowDown(World world, double x, double y, double z) {
		boolean shouldSlowDown = false;
		int i = MathHelper.floor(x);
		int j = MathHelper.floor(y);
		int k = MathHelper.floor(z);

		if(world.getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS))
			--j;

		BlockPos pos = new BlockPos(i, j, k);
		BlockState state = world.getBlockState(pos);

		if(AbstractRailBlock.isRail(state)) {
			Pair<Vec3i, Vec3i> rails = AbstractMinecartEntity.getAdjacentRailPositionsByShape(state.get(((AbstractRailBlock) state.getBlock()).getShapeProperty()));
			BlockState railState1 = world.getBlockState(pos.add(rails.getFirst()));
			BlockState railState2 = world.getBlockState(pos.add(rails.getSecond()));

			if(railState1.isIn(BlockTags.RAILS) && railState1.getBlock() instanceof AbstractRailBlock rail) {
				switch(railState1.get(rail.getShapeProperty())) {
					case NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST -> shouldSlowDown = true;
				}
			}

			if(railState2.isIn(BlockTags.RAILS) && railState2.getBlock() instanceof AbstractRailBlock rail) {
				switch(railState2.get(rail.getShapeProperty())) {
					case NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST -> shouldSlowDown = true;
				}
			}
		}

		return shouldSlowDown;
	}
}
