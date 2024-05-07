package bl4ckscor3.mod.xptome.datagen;

import java.util.concurrent.CompletableFuture;

import bl4ckscor3.mod.xptome.XPTome;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemTagGenerator extends ItemTagsProvider {
	public ItemTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, CompletableFuture.supplyAsync(() -> null), XPTome.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider lookupProvider) {
		tag(ItemTags.BOOKSHELF_BOOKS).add(XPTome.XP_TOME.get());
	}
}
