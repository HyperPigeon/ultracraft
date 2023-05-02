package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.accessor.WingedPlayerEntity;
import absolutelyaya.ultracraft.client.UltracraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public class ClientLivingEntityMixin
{
	@ModifyConstant(method = "tickMovement", constant = @Constant(floatValue = 0.98f))
	float modifySlowdown(float constant)
	{
		if(this instanceof WingedPlayerEntity winged && winged instanceof ClientPlayerEntity)
			return winged.shouldIgnoreSlowdown() ? 1f : constant;
		else
			return constant;
	}
	
	@Inject(method = "getJumpVelocity", at = @At("RETURN"), cancellable = true)
	void onGetJumpVelocity(CallbackInfoReturnable<Float> cir)
	{
		if(this instanceof WingedPlayerEntity winged && winged.isWingsVisible())
			cir.setReturnValue(cir.getReturnValue() + 0.1f * Math.max(UltracraftClient.jumpBoost, 0));
	}
}
