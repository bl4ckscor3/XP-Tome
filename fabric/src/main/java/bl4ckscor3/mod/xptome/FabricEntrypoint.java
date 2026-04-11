package bl4ckscor3.mod.xptome;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.fml.config.ModConfig;

public class FabricEntrypoint implements ModInitializer, Platform {
	private static final Function<FabricCreativeModeTabOutput, XPTome.CreativeTabInserter> INSERTER = output -> (existingEntry, newEntry, visibility) -> output.insertAfter(existingEntry, List.of(newEntry), visibility);

	@Override
	public void onInitialize() {
		ConfigRegistry.INSTANCE.register(XPTome.MODID, ModConfig.Type.STARTUP, Configuration.CONFIG_SPEC, "xptome.toml");
		XPTome.initialize(this);
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(output -> XPTome.functionalBlocksCreativeTabEntry(INSERTER.apply(output)));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> XPTome.ingredientsCreativeTabEntry(INSERTER.apply(output)));
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public <R, T extends R> void register(ResourceKey<? extends Registry<R>> registryKey, Supplier<T> entry, String path) {
		Optional<Holder.Reference<R>> registry = BuiltInRegistries.REGISTRY.get((ResourceKey) registryKey);

		if (registry.isEmpty()) {
			throw new IllegalArgumentException("Couldn't find registry " + registryKey);
		}

		Registry.register((Registry<R>) registry.get().value(), XPTome.id(path), entry.get());
	}
}
