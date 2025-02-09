package absolutelyaya.ultracraft.client.rendering.entity.machine;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.entity.machine.SwordsmachineEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SwordsmachineModel extends GeoModel<SwordsmachineEntity>
{
	@Override
	public Identifier getModelResource(SwordsmachineEntity object)
	{
		return new Identifier(Ultracraft.MOD_ID, "geo/entities/swordsmachine.geo.json");
	}
	
	@Override
	public Identifier getTextureResource(SwordsmachineEntity object)
	{
		return new Identifier(Ultracraft.MOD_ID, "textures/entity/swordsmachine.png");
	}
	
	@Override
	public Identifier getAnimationResource(SwordsmachineEntity animatable)
	{
		return new Identifier(Ultracraft.MOD_ID, "animations/entities/swordsmachine.animation.json");
	}
	
	@Override
	public void setCustomAnimations(SwordsmachineEntity animatable, long instanceId, AnimationState<SwordsmachineEntity> animationState)
	{
		super.setCustomAnimations(animatable, instanceId, animationState);
		CoreGeoBone head = this.getAnimationProcessor().getBone("head");
		CoreGeoBone shotgunArm = this.getAnimationProcessor().getBone("left_arm");
		
		float f = ((float) Math.PI / 180F);
		if(MinecraftClient.getInstance().isPaused())
			return;
		
		shotgunArm.setHidden(!animatable.hasShotgun());
		
		EntityModelData extraData = (EntityModelData)animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
		if(head != null && animatable.getAnimation() != 1)
		{
			head.setRotX(head.getRotX() + extraData.headPitch() * f);
			head.setRotY(extraData.netHeadYaw() * f);
		}
	}
}
