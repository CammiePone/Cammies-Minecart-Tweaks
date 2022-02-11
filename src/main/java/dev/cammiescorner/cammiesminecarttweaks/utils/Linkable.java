package dev.cammiescorner.cammiesminecarttweaks.utils;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;

public interface Linkable {
	AbstractMinecartEntity getLinkedParent();
	void setLinkedParent(AbstractMinecartEntity parent);
}
