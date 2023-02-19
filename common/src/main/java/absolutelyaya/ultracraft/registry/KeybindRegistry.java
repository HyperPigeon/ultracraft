package absolutelyaya.ultracraft.registry;

import absolutelyaya.ultracraft.client.UltracraftClient;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindRegistry
{
	public static final KeyBinding HIGH_VELOCITY_TOGGLE = new KeyBinding("key.ultracraft.hivel_toggle", InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_V, "category.ultracraft");
	
	public static void register()
	{
		KeyMappingRegistry.register(HIGH_VELOCITY_TOGGLE);
		
		ClientTickEvent.CLIENT_POST.register(client -> {
			while(HIGH_VELOCITY_TOGGLE.wasPressed())
			{
				//TODO: add High Velocity Mode (dashing & sliding)
				//TODO: Visual Indicator for HiVelMode
				UltracraftClient.toggleHiVelEnabled();
			}
		});
	}
}
