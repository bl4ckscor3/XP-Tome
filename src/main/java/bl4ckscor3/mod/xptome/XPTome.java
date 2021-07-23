package bl4ckscor3.mod.xptome;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(XPTome.MODID)
@EventBusSubscriber(modid=XPTome.MODID)
public class XPTome
{
	public static final String MODID = "xpbook";
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
	/** @deprecated This is kept for legacy reasons. Use the field below this one. */
	@Deprecated
	public static final RegistryObject<Item> XP_BOOK = ITEMS.register("xp_book", () -> new OldXPTomeItem(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<Item> XP_TOME = ITEMS.register("xp_tome", () -> new XPTomeItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)));

	public XPTome()
	{
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Configuration.CONFIG_SPEC, "xptome-server.toml");
	}

	@SubscribeEvent
	public static void onAnvilUpdate(AnvilUpdateEvent event)
	{
		if(event.getLeft().getItem() == XP_BOOK.get() || event.getRight().getItem() == XP_BOOK.get())
			event.setCanceled(true);
		else if(event.getLeft().getItem() == XP_TOME.get() || event.getRight().getItem() == XP_TOME.get())
			event.setCanceled(true);
	}
}
