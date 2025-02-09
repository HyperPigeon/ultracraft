package absolutelyaya.ultracraft.particle;

import absolutelyaya.ultracraft.particle.goop.SpriteAAParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class TeleportParticle extends SpriteAAParticle
{
	protected TeleportParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider, double size)
	{
		super(world, x, y + 1f + size / 2f, z, spriteProvider);
		scale = new Vec3d(1f, 1f, 1f).multiply(size);
		maxAge = 8;
		setSprite(spriteProvider.getSprite(age, maxAge));
	}
	
	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	@Override
	protected int getBrightness(float tint)
	{
		return 15728880;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		setSpriteForAge(spriteProvider);
	}
	
	public record TeleportParticleFactory(SpriteProvider sprite) implements ParticleFactory<TeleportParticleEffect>
	{
		@Nullable
		@Override
		public Particle createParticle(TeleportParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			TeleportParticle p = new TeleportParticle(world, x, y, z, sprite, parameters.size);
			p.setSprite(sprite);
			return p;
		}
	}
}
