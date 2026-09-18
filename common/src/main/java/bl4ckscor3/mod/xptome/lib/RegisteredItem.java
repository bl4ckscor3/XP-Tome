package bl4ckscor3.mod.xptome.lib;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import bl4ckscor3.mod.xptome.XPTome;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public record RegisteredItem<T extends Item>(ResourceKey<Item> key, Supplier<T> object) implements RegistryObject<Item, T>, ItemLike {
	public static <I extends Item> RegisteredItem<I> item(Identifier id, ItemConstructor<I> itemConstructor, Supplier<Item.Properties> properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
		return new RegisteredItem<>(
			key,
			Suppliers.memoize(() -> itemConstructor.construct(properties.get().setId(key)))
		);
	}

	public static <I extends Item> RegisteredItem<I> item(String id, ItemConstructor<I> itemConstructor, Supplier<Item.Properties> properties) {
		return item(XPTome.id(id), itemConstructor, properties);
	}

	@Override
	public Item asItem() {
		return get();
	}

	@FunctionalInterface
	public interface ItemConstructor<I extends Item> {
		I construct(Item.Properties properties);
	}
}
