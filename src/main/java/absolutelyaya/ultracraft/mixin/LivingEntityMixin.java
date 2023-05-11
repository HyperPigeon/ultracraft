package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.accessor.LivingEntityAccessor;
import absolutelyaya.ultracraft.accessor.WingedPlayerEntity;
import absolutelyaya.ultracraft.damage.DamageSources;
import absolutelyaya.ultracraft.damage.DamageTypeTags;
import absolutelyaya.ultracraft.entity.AbstractUltraHostileEntity;
import absolutelyaya.ultracraft.registry.GameruleRegistry;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Supplier;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccessor
{
	@Shadow public abstract boolean isPushable();
	
	@Shadow public abstract void swingHand(Hand hand);
	
	Supplier<Boolean> canBleedSupplier = () -> true, takePunchKnockpackSupplier = this::isPushable; //TODO: add Sandy Enemies (eventually)
	int punchTicks, punchDuration = 6;
	boolean punching;
	float punchProgress, prevPunchProgress, recoil;
	
	boolean timeFrozen;
	
	public LivingEntityMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}
	
	@Inject(method = "tick", at = @At("HEAD"))
	void onTick(CallbackInfo ci)
	{
		timeFrozen = Ultracraft.isTimeFrozen();
		if(!timeFrozen || punchTicks < 2)
			punchTick();
		recoil = MathHelper.lerp(0.3f, recoil, 0f);
	}
	
	@SuppressWarnings("EqualsBetweenInconvertibleTypes")
	@Inject(method = "damage", at = @At("RETURN"))
	void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
	{
		if(!cir.getReturnValue() || world.isClient || !IsCanBleed())
			return;
		if(source.isIn(DamageTypeTags.ULTRACRAFT) && !((Object)this instanceof AbstractUltraHostileEntity) && !((Object)this instanceof PlayerEntity))
		{
			System.out.println("AAAAAAAA");
			amount *= 2.5f;
		}
		List<PlayerEntity> nearby = world.getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), getBoundingBox().expand(32), e -> true);
		List<PlayerEntity> heal = world.getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), getBoundingBox().expand(2), e -> !e.equals(this));
		for (PlayerEntity player : nearby)
		{
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeFloat(amount);
			buf.writeDouble(getPos().x);
			buf.writeDouble(getPos().y);
			buf.writeDouble(getPos().z);
			buf.writeDouble(getHeight() / 2);
			buf.writeBoolean(source.isOf(DamageSources.SHOTGUN));
			ServerPlayNetworking.send((ServerPlayerEntity)player, PacketRegistry.BLEED_PACKET_ID, buf);
		}
		for (PlayerEntity player : heal)
		{
			player.heal(amount / 1.5f);
			player.getHungerManager().add((int)(amount / 1.5f), 5f);
		}
		if(source.isOf(DamageSources.GUN) || source.isOf(DamageSources.SHOTGUN))
			timeUntilRegen = 9;
		if(source.isOf(DamageSources.SWORDSMACHINE))
			timeUntilRegen = 12;
	}
	
	@Inject(method = "getJumpVelocity", at = @At("RETURN"), cancellable = true)
	void onGetJumpVel(CallbackInfoReturnable<Float> cir)
	{
		if(this instanceof WingedPlayerEntity winged && winged.isWingsVisible())
		{
			if(!world.isClient)
				cir.setReturnValue(cir.getReturnValue() + 0.1f * Math.max(world.getGameRules().getInt(GameruleRegistry.HIVEL_JUMP_BOOST), 0));
		}
	}
	
	void punchTick()
	{
		int i = punchDuration;
		if (punching)
		{
			++punchTicks;
			if (punchTicks >= i)
			{
				punchTicks = 0;
				punching = false;
			}
			else if(timeFrozen)
				punchTicks = 1;
		}
		else
			punchTicks = 0;
		
		prevPunchProgress = punchProgress;
		punchProgress = (float)punchTicks / (float)i;
		if(timeFrozen)
			prevPunchProgress = punchProgress;
	}
	
	@Override
	public boolean Punch()
	{
		if(!punching)
		{
			punchTicks = 0;
			punching = true;
			if(!world.isClient)
				swingHand(Hand.OFF_HAND);
			return true;
		}
		return false;
	}
	
	@Override
	public float GetPunchProgress(float tickDelta)
	{
		float f = punchProgress - prevPunchProgress;
		if (f < 0.0F) {
			++f;
		}
		
		if (Ultracraft.isTimeFrozen())
			return punchProgress;
		else
			return prevPunchProgress + f * tickDelta;
	}
	
	@Override
	public boolean IsPunching()
	{
		return punching;
	}
	
	@Override
	public boolean IsCanBleed()
	{
		return canBleedSupplier.get();
	}
	
	@Override
	public void SetCanBleedSupplier(Supplier<Boolean> supplier)
	{
		canBleedSupplier = supplier;
	}
	
	@Override
	public boolean takePunchKnockback()
	{
		return takePunchKnockpackSupplier.get();
	}
	
	@Override
	public void SetTakePunchKnockbackSupplier(Supplier<Boolean> supplier)
	{
		takePunchKnockpackSupplier = supplier;
	}
	
	@Override
	public void addRecoil(float recoil)
	{
		this.recoil += recoil;
	}
	
	@Override
	public float getRecoil()
	{
		return recoil;
	}
}
