package bl4ckscor3.mod.xptome;

import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;

public interface Platform {
	<R, T extends R> void register(ResourceKey<? extends Registry<R>> registry, Supplier<T> entry, String path);

	default <R, T extends R> void register(ResourceKey<? extends Registry<R>> registry, RegistryObject<T> registryObject) {
		register(registry, registryObject.object(), registryObject.id().getPath());
	}

	default void onXpChange(Player player, int amount) {
	}

	default void onLevelChange(Player player, int experienceLevel) {
	}
}
