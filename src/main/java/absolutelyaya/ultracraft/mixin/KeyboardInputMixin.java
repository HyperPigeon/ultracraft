package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.accessor.WingedPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input
{
	@Inject(method = "tick", at = @At("TAIL"))
	void onTick(boolean slowDown, float f, CallbackInfo ci)
	{
		PlayerEntity player = MinecraftClient.getInstance().player;
		if(player != null && ((WingedPlayerEntity)player).isWingsVisible() && player.isSprinting())
			movementForward = 0f;
	}
	//TODO: align strafing movement with slide direction
}
