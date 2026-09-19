package bl4ckscor3.mod.xptome.datagen;

import java.util.Set;

import bl4ckscor3.mod.xptome.XPTome;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = XPTome.MODID)
public class DataGenRegistrar {
	private DataGenRegistrar() {}

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent.Client event) {
		event.createProvider(ItemTagGenerator::new);
		event.createReloadableRegistryObjects(
			new RegistrySetBuilder().add(RecipeProvider.asBootstrap(RecipeGenerator::new)),
			Set.of(XPTome.MODID)
		);
	}
}
