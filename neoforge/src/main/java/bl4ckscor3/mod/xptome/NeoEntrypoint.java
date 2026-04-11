package bl4ckscor3.mod.xptome;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(XPTome.MODID)
@EventBusSubscriber
public class NeoEntrypoint implements Platform {
	private final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> registers = new HashMap<>();
	private final IEventBus modBus;

	public NeoEntrypoint(ModContainer modContainer, IEventBus modBus) {
		this.modBus = modBus;
		XPTome.initialize(this);
		modContainer.registerConfig(ModConfig.Type.STARTUP, Configuration.CONFIG_SPEC, "xptome.toml");
	}

	@SubscribeEvent
	public static void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS))
			XPTome.functionalBlocksCreativeTabEntry(event::insertAfter);
		else if (event.getTabKey().equals(CreativeModeTabs.INGREDIENTS))
			XPTome.ingredientsCreativeTabEntry(event::insertAfter);
	}

	@Override
	public void onXpChange(Player player, int amount) {
		NeoForge.EVENT_BUS.post(new PlayerXpEvent.XpChange(player, amount));
	}

	@Override
	public void onLevelChange(Player player, int experienceLevel) {
		NeoForge.EVENT_BUS.post(new PlayerXpEvent.LevelChange(player, player.experienceLevel));
	}

	@Override
	public <R, T extends R> void register(ResourceKey<? extends Registry<R>> registry, Supplier<T> entry, String path) {
		@SuppressWarnings("unchecked")
		DeferredRegister<R> register = (DeferredRegister<R>) registers.computeIfAbsent(
			registry,
			_ -> {
				DeferredRegister<R> r = DeferredRegister.create(registry, XPTome.MODID);

				r.register(modBus);
				return r;
			}
		);
		register.register(path, entry);
	}
}
