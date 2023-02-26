package dev.cammiescorner.cammiesminecarttweaks.api;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface injected onto AbstractMinecartEntity to facilitate linking carts together.
 */
public interface Linkable {
	@Nullable AbstractMinecartEntity getLinkedParent();
	void setLinkedParent(@Nullable AbstractMinecartEntity parent);

	@Nullable AbstractMinecartEntity getLinkedChild();
	void setLinkedChild(@Nullable AbstractMinecartEntity child);

	void setLinkedParentClient(int id);
	void setLinkedChildClient(int id);

	AbstractMinecartEntity asAbstractMinecartEntity();

	static void setParentChild(@NotNull Linkable parent, @NotNull Linkable child) {
		unsetParentChild(parent, parent.getLinkedChild());
		unsetParentChild(child, child.getLinkedParent());
		parent.setLinkedChild(child.asAbstractMinecartEntity());
		child.setLinkedParent(parent.asAbstractMinecartEntity());
	}

	static void unsetParentChild(@Nullable Linkable parent, @Nullable Linkable child) {
		if (parent != null) {
			parent.setLinkedChild(null);
		}
		if (child != null) {
			child.setLinkedParent(null);
		}
	}
}
