package bl4ckscor3.mod.xptome;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@Mod(modid=XPTome.MODID, name="XP Tome", acceptedMinecraftVersions="[1.12.2]")
@EventBusSubscriber
public class XPTome
{
	public static final String MODID = "xpbook";
	@ObjectHolder(MODID + ":" + ItemXPTome.NAME)
	public static final Item XP_BOOK = null;

	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(new ItemXPTome().setRegistryName(new ResourceLocation(MODID, ItemXPTome.NAME)).setTranslationKey(MODID + ":" + ItemXPTome.NAME));
	}

	@SubscribeEvent
	public static void onAnvilUpdate(AnvilUpdateEvent event)
	{
		if(event.getLeft().getItem() == XP_BOOK || event.getRight().getItem() == XP_BOOK)
			event.setCanceled(true);
	}
}
