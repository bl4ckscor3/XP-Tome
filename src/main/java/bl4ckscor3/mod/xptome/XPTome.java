package bl4ckscor3.mod.xptome;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;

@Mod(XPTome.MODID)
@EventBusSubscriber(modid=XPTome.MODID, bus=Bus.MOD)
public class XPTome
{
	public static final String MODID = "xpbook";
	@ObjectHolder(MODID + ":" + ItemXPTome.NAME)
	public static final Item XP_BOOK = null;

	public XPTome()
	{
		MinecraftForge.EVENT_BUS.addListener(this::onAnvilUpdate);
	}

	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(new ItemXPTome().setRegistryName(new ResourceLocation(MODID, ItemXPTome.NAME)));
	}

	@SubscribeEvent
	public void onAnvilUpdate(AnvilUpdateEvent event)
	{
		if(event.getLeft().getItem() == XP_BOOK || event.getRight().getItem() == XP_BOOK)
			event.setCanceled(true);
	}
}
