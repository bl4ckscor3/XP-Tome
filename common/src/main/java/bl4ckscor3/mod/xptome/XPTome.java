package bl4ckscor3.mod.xptome;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class XPTome {
	public static final String MODID = "xpbook";
	private static Platform platform;
	public static final Supplier<DataComponentType<Integer>> STORED_XP = Suppliers.memoize(() -> DataComponentType.<Integer>builder().persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).cacheEncoding().build());
	public static final Supplier<DataComponentType<Integer>> MAXIMUM_XP = Suppliers.memoize(() -> DataComponentType.<Integer>builder().persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).cacheEncoding().build());
	public static final Supplier<DataComponentType<Double>> RETRIEVAL_PERCENTAGE = Suppliers.memoize(() -> DataComponentType.<Double>builder().persistent(Codec.DOUBLE.validate(XPTome::validateRetrievalPercentage)).networkSynchronized(ByteBufCodecs.DOUBLE).cacheEncoding().build());
	public static final Supplier<DataComponentType<Unit>> RETRIEVE_XP_ORBS = Suppliers.memoize(() -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).cacheEncoding().build());
	public static final Supplier<DataComponentType<Unit>> RETRIEVE_UNTIL_NEXT_LEVEL = Suppliers.memoize(() -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).cacheEncoding().build());
	public static final Supplier<DataComponentType<Unit>> STORE_UNTIL_PREVIOUS_LEVEL = Suppliers.memoize(() -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).cacheEncoding().build());
	/** @deprecated This is kept for legacy reasons. Use the field below this one. */
	@Deprecated
	public static final RegistryObject<OldXPTomeItem> XP_BOOK = RegistryObject.item("xp_book", OldXPTomeItem::new, () -> new Item.Properties().stacksTo(1));
	public static final RegistryObject<XPTomeItem> XP_TOME = RegistryObject.item("xp_tome", p -> new XPTomeItem(Configuration.CONFIG.applyToDefaultComponents(p.component(STORED_XP.get(), 0))), () -> new Item.Properties().stacksTo(1));

	public synchronized static void initialize(Platform platform) {
		if (XPTome.platform != null) {
			throw new IllegalArgumentException(MODID + " platform has already been initialized");
		}

		XPTome.platform = platform;
		platform.register(Registries.DATA_COMPONENT_TYPE, STORED_XP, "stored_xp");
		platform.register(Registries.DATA_COMPONENT_TYPE, MAXIMUM_XP, "max_xp");
		platform.register(Registries.DATA_COMPONENT_TYPE, RETRIEVAL_PERCENTAGE, "retrieval_percentage");
		platform.register(Registries.DATA_COMPONENT_TYPE, RETRIEVE_XP_ORBS, "retrieve_xp_orbs");
		platform.register(Registries.DATA_COMPONENT_TYPE, RETRIEVE_UNTIL_NEXT_LEVEL, "retrieve_until_next_level");
		platform.register(Registries.DATA_COMPONENT_TYPE, STORE_UNTIL_PREVIOUS_LEVEL, "store_until_previous_level");
		platform.register(Registries.ITEM, XP_BOOK);
		platform.register(Registries.ITEM, XP_TOME);
	}

	public static void onAnvilUpdate(ItemStack left, ItemStack right, Runnable onCancel) {
		Item xpBook = XP_BOOK.get();
		Item xpTome = XP_TOME.get();

		if (left.is(xpBook) || right.is(xpBook))
			onCancel.run();

		if (left.is(xpTome) || right.is(xpTome))
			onCancel.run();
	}

	public static void functionalBlocksCreativeTabEntry(CreativeTabInserter inserter) {
		inserter.insertAfter(new ItemStack(Blocks.ENCHANTING_TABLE), new ItemStack(XP_TOME.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
	}

	public static void ingredientsCreativeTabEntry(CreativeTabInserter inserter) {
		inserter.insertAfter(new ItemStack(Items.BOOK), new ItemStack(XP_TOME.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
	}

	private static DataResult<Double> validateRetrievalPercentage(double value) {
		return value >= 0.0D && value <= 1.0D ? DataResult.success(value) : DataResult.error(() -> "Value must be within range [0.0;1.0]: " + value);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MODID, path);
	}

	public static Platform platform() {
		return platform;
	}

	@FunctionalInterface
	public interface CreativeTabInserter {
		void insertAfter(ItemStack existingEntry, ItemStack newEntry, CreativeModeTab.TabVisibility visibility);
	}
}
