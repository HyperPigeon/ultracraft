package absolutelyaya.ultracraft;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class EarlyRiser implements Runnable
{
	@Override
	public void run()
	{
		MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();
		
		String entityPose = remapper.mapClassName("intermediary", "net.minecraft.class_4050");
		ClassTinkerers.enumBuilder(entityPose).addEnum("DASH").addEnum("SLIDE").build();
	}
}
