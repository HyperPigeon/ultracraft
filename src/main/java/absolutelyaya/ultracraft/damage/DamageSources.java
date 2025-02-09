package absolutelyaya.ultracraft.damage;

import absolutelyaya.ultracraft.Ultracraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DamageSources
{
	public static final RegistryKey<DamageType> MAURICE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Ultracraft.MOD_ID, "maurice"));
	public static final RegistryKey<DamageType> GUN = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Ultracraft.MOD_ID, "gun"));
	public static final RegistryKey<DamageType> SHOTGUN = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Ultracraft.MOD_ID, "shotgun"));
	public static final RegistryKey<DamageType> SHOCKWAVE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Ultracraft.MOD_ID, "shockwave"));
	public static final RegistryKey<DamageType> SWORDSMACHINE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Ultracraft.MOD_ID, "swordsmachine"));
	public static final RegistryKey<DamageType> POUND = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Ultracraft.MOD_ID, "groundpound"));
	public static final RegistryKey<DamageType> PARRY = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Ultracraft.MOD_ID, "parry"));
	public static final RegistryKey<DamageType> PARRYAOE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Ultracraft.MOD_ID, "parry_collateral"));
	public static final RegistryKey<DamageType> INTERRUPT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Ultracraft.MOD_ID, "interrupt"));
	public static final RegistryKey<DamageType> PROJBOOST = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Ultracraft.MOD_ID, "projectile_boost"));
	
	public static DamageSource get(World world, RegistryKey<DamageType> type)
	{
		return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(type));
	}
	
	public static DamageSource get(World world, RegistryKey<DamageType> type, Entity attacker)
	{
		return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(type), attacker);
	}
}
