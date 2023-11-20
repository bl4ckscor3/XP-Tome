package bl4ckscor3.mod.xptome;

import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(XPTome.MODID)
@EventBusSubscriber(modid = XPTome.MODID)
public class XPTome {
	public static final String MODID = "xpbook";
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
	/** @deprecated This is kept for legacy reasons. Use the field below this one. */
	@Deprecated
	public static final DeferredItem<OldXPTomeItem> XP_BOOK = ITEMS.register("xp_book", () -> new OldXPTomeItem(new Item.Properties().stacksTo(1)));
	public static final DeferredItem<XPTomeItem> XP_TOME = ITEMS.register("xp_tome", () -> new XPTomeItem(new Item.Properties().stacksTo(1)));

	public XPTome(IEventBus modBus) {
		ITEMS.register(modBus);
		modBus.addListener(this::onCreativeModeTabBuildContents);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Configuration.CONFIG_SPEC, "xptome-server.toml");
	}

	@SubscribeEvent
	public static void onAnvilUpdate(AnvilUpdateEvent event) {
		//prevention for a crash that should theoretically not happen, but apparently does
		XP_BOOK.asOptional().ifPresent(xpBook -> {
			if (event.getLeft().is(xpBook) || event.getRight().is(xpBook))
				event.setCanceled(true);
		});
		XP_TOME.asOptional().ifPresent(xpTome -> {
			if (event.getLeft().is(xpTome) || event.getRight().is(xpTome))
				event.setCanceled(true);
		});
	}

	public void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS)
			event.getEntries().putAfter(new ItemStack(Blocks.ENCHANTING_TABLE), new ItemStack(XP_TOME.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
		else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
			event.getEntries().putAfter(new ItemStack(Items.BOOK), new ItemStack(XP_TOME.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
	}
}
