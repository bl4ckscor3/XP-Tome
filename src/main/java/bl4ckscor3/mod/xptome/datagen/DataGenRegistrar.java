package bl4ckscor3.mod.xptome.datagen;

import bl4ckscor3.mod.xptome.XPTome;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = XPTome.MODID)
public class DataGenRegistrar {
	private DataGenRegistrar() {}

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent.Client event) {
		event.createProvider(ItemTagGenerator::new);
		event.createProvider(RecipeGenerator.Runner::new);
	}
}
