package bl4ckscor3.mod.xptome;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public record RegistryObject<T>(Identifier id, Supplier<T> object) {
	public static <I extends Item> RegistryObject<I> item(String id, ItemConstructor<I> itemConstructor, Supplier<Item.Properties> properties) {
		Identifier key = XPTome.id(id);
		return new RegistryObject<>(
			key,
			Suppliers.memoize(() -> itemConstructor.construct(properties.get().setId(ResourceKey.create(Registries.ITEM, key))))
		);
	}

	public T get() {
		return object.get();
	}

	@FunctionalInterface
	public interface ItemConstructor<I extends Item> {
		I construct(Item.Properties properties);
	}
}
