package bl4ckscor3.mod.xptome;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod(XPTome.MODID)
@EventBusSubscriber(modid=XPTome.MODID, bus=Bus.MOD)
public class XPTome
{
	public static final String MODID = "xpbook";

	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(new ItemXPTome().setRegistryName(new ResourceLocation(MODID, ItemXPTome.NAME)));
	}
}
