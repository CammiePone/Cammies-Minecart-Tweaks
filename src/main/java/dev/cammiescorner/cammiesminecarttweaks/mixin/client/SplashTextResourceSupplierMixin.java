package dev.cammiescorner.cammiesminecarttweaks.mixin.client;

import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SplashTextResourceSupplier.class)
public abstract class SplashTextResourceSupplierMixin extends SinglePreparationResourceReloader<List<String>> {
	@Shadow @Final private List<String> splashTexts;

	@Inject(method = "apply(Ljava/util/List;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("TAIL"))
	public void minecarttweaks$suffering(List<String> list, ResourceManager resourceManager, Profiler profiler, CallbackInfo info) {
		splashTexts.add("Murder on the Minecart Express!");
	}
}
