package bl4ckscor3.mod.xptome.datagen;

import bl4ckscor3.mod.xptome.XPTome;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

@EventBusSubscriber(modid = XPTome.MODID)
public class DataGenRegistrar {
	private DataGenRegistrar() {}

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent.Client event) {
		event.createProvider(ItemTagGenerator::new);
	}

	@SubscribeEvent
	public static void onGatherDataRegistryEntries(GatherDataRegistryEntriesEvent event) {
		event.add(RecipeProvider.asBootstrap(RecipeGenerator::new));
	}
}
