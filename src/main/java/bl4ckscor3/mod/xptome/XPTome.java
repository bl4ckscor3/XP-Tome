package bl4ckscor3.mod.xptome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(XPTome.MODID)
@EventBusSubscriber(modid = XPTome.MODID)
public class XPTome {
	public static final String MODID = "xpbook";
	public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(MODID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> STORED_XP = DATA_COMPONENTS.registerComponentType("stored_xp", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).cacheEncoding());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAXIMUM_XP = DATA_COMPONENTS.registerComponentType("max_xp", builder -> builder.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).cacheEncoding());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> RETRIEVAL_PERCENTAGE = DATA_COMPONENTS.registerComponentType("retrieval_percentage", builder -> builder.persistent(Codec.DOUBLE.validate(XPTome::validateRetrievalPercentage)).networkSynchronized(ByteBufCodecs.DOUBLE).cacheEncoding());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> RETRIEVE_XP_ORBS = DATA_COMPONENTS.registerComponentType("retrieve_xp_orbs", builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).cacheEncoding());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> RETRIEVE_UNTIL_NEXT_LEVEL = DATA_COMPONENTS.registerComponentType("retrieve_until_next_level", builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).cacheEncoding());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> STORE_UNTIL_PREVIOUS_LEVEL = DATA_COMPONENTS.registerComponentType("store_until_previous_level", builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).cacheEncoding());
	/** @deprecated This is kept for legacy reasons. Use the field below this one. */
	@Deprecated
	public static final DeferredItem<OldXPTomeItem> XP_BOOK = ITEMS.register("xp_book", () -> new OldXPTomeItem(new Item.Properties().stacksTo(1)));
	public static final DeferredItem<XPTomeItem> XP_TOME = ITEMS.register("xp_tome", () -> new XPTomeItem(Configuration.CONFIG.applyToDefaultComponents(new Item.Properties().stacksTo(1).component(STORED_XP, 0))));

	public XPTome(IEventBus modBus, ModContainer container) {
		container.registerConfig(ModConfig.Type.STARTUP, Configuration.CONFIG_SPEC, "xptome.toml");
		DATA_COMPONENTS.register(modBus);
		ITEMS.register(modBus);
		modBus.addListener(this::onCreativeModeTabBuildContents);
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
		if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS))
			event.insertAfter(new ItemStack(Blocks.ENCHANTING_TABLE), new ItemStack(XP_TOME.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
		else if (event.getTabKey().equals(CreativeModeTabs.INGREDIENTS))
			event.insertAfter(new ItemStack(Items.BOOK), new ItemStack(XP_TOME.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
	}

	private static DataResult<Double> validateRetrievalPercentage(double value) {
		return value >= 0.0D && value <= 1.0D ? DataResult.success(value) : DataResult.error(() -> "Value must be within range [0.0;1.0]: " + value);
	}
}
