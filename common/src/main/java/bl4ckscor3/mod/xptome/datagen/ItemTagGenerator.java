package bl4ckscor3.mod.xptome.datagen;

import java.util.concurrent.CompletableFuture;

import bl4ckscor3.mod.xptome.XPTome;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.tags.ItemTags;

public class ItemTagGenerator extends VanillaItemTagsProvider {
	public ItemTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
	}

	@Override
	protected void addTags(HolderLookup.Provider lookupProvider) {
		tag(ItemTags.BOOKSHELF_BOOKS).add(XPTome.XP_TOME.get());
	}
}
