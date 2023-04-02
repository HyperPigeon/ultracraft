package absolutelyaya.ultracraft;

import absolutelyaya.ultracraft.registry.DamageSources;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ServerHitscanHandler
{
	public static void sendPacket(ServerWorld world, Vec3d from, Vec3d to, byte type)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeDouble(from.x);
		buf.writeDouble(from.y);
		buf.writeDouble(from.z);
		buf.writeDouble(to.x);
		buf.writeDouble(to.y);
		buf.writeDouble(to.z);
		buf.writeByte(type);
		for (ServerPlayerEntity player : world.getPlayers())
			ServerPlayNetworking.send(player, PacketRegistry.HITSCAN_PACKET_ID, buf);
	}
	
	public static void performHitscan(LivingEntity user, byte type, int damage)
	{
		performHitscan(user, type, damage, 1, 0);
	}
	
	public static void performHitscan(LivingEntity user, byte type, int damage, int maxHits)
	{
		performHitscan(user, type, damage, maxHits, 0);
	}
	
	public static void performHitscan(LivingEntity user, byte type, int damage, float explosionPower)
	{
		performHitscan(user, type, damage, 1, explosionPower);
	}
	
	public static void performHitscan(LivingEntity user, byte type, int damage, int maxHits, float explosionPower)
	{
		World world = user.getWorld();
		Vec3d origin = user.getPos().add(new Vec3d(0f, user.getStandingEyeHeight(), 0f));
		Vec3d from = origin;
		Vec3d origunalTo = user.getPos().add(0f, user.getStandingEyeHeight(), 0f).add(user.getRotationVec(0.5f).multiply(64.0));
		Vec3d modifiedTo;
		BlockHitResult bHit = world.raycast(new RaycastContext(from, origunalTo, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, user));
		origunalTo = bHit.getPos();
		modifiedTo = origunalTo;
		List<Entity> entities = new ArrayList<>();
		Box box = user.getBoundingBox().stretch(user.getRotationVec(0.5f).multiply(64.0)).expand(1.0, 1.0, 1.0);
		boolean searchForEntities = true;
		while (searchForEntities)
		{
			EntityHitResult eHit = ProjectileUtil.raycast(user, from, origunalTo, box,
					(entity) -> (!entities.contains(entity) && !(entity instanceof ProjectileEntity)), 64f * 64f);
			if(eHit == null)
				break;
			searchForEntities = eHit.getType() != HitResult.Type.MISS && maxHits > 0;
			if(searchForEntities)
			{
				maxHits--;
				from = eHit.getPos();
				if(maxHits == 0)
					modifiedTo = eHit.getPos();
				entities.add(eHit.getEntity());
			}
		}
		entities.forEach((e) -> e.damage(DamageSources.getGun(user), damage));
		if(explosionPower > 0f)
			world.createExplosion(null, modifiedTo.x, modifiedTo.y, modifiedTo.z, explosionPower, World.ExplosionSourceType.NONE);
		sendPacket((ServerWorld)user.world, origin.add(new Vec3d(-0.5f, -0.3f, 0f).rotateY(-(float)Math.toRadians(user.getYaw()))), modifiedTo, type);
	}
}
