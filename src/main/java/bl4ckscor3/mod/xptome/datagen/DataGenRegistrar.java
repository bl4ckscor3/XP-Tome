package bl4ckscor3.mod.xptome.datagen;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import bl4ckscor3.mod.xptome.XPTome;
import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = XPTome.MODID, bus = Bus.MOD)
public class DataGenRegistrar {
	private DataGenRegistrar() {}

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(event.includeServer(), new ItemTagGenerator(output, lookupProvider, event.getExistingFileHelper()));
		generator.addProvider(event.includeServer(), new RecipeGenerator.Runner(output, lookupProvider));
		//@formatter:off
		generator.addProvider(true, new PackMetadataGenerator(output)
				.add(PackMetadataSection.TYPE, new PackMetadataSection(Component.literal("XP Tome resources & data"),
						DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES),
						Optional.of(new InclusiveRange<>(0, Integer.MAX_VALUE)))));
		//@formatter:on
	}
}
