package absolutelyaya.ultracraft.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class MaliciousChargeParticle extends BillboardStripeParticle
{
	protected MaliciousChargeParticle(ClientWorld clientWorld, double x, double y, double z)
	{
		super(clientWorld, x, y, z);
	}
	
	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	public void setSprite(SpriteProvider spriteProvider) {
		this.setSprite(spriteProvider.getSprite(this.random));
	}
	
	public record MaliciousChargeParticleFactory(SpriteProvider spriteProvider) implements ParticleFactory<DefaultParticleType>
	{
		@Override
		public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			MaliciousChargeParticle particle = new MaliciousChargeParticle(world, x, y, z);
			particle.setSprite(spriteProvider);
			particle.width = 0.25f;
			particle.maxLength = 0.8f;
			particle.maxAge = 40;
			particle.setColor(1f, 1f, 1f);
			particle.setVelocity(velocityX, velocityY, velocityZ);
			return particle;
		}
	}
}
