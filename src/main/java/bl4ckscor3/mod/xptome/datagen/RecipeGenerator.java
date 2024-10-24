package bl4ckscor3.mod.xptome.datagen;

import java.util.concurrent.CompletableFuture;

import bl4ckscor3.mod.xptome.XPTome;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class RecipeGenerator extends RecipeProvider {
	private final HolderGetter<Item> items;

	public RecipeGenerator(HolderLookup.Provider lookupProvider, RecipeOutput output) {
		super(lookupProvider, output);
		items = lookupProvider.lookupOrThrow(Registries.ITEM);
	}

	@Override
	protected final void buildRecipes() {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, XPTome.XP_TOME)
		.pattern(" E ")
		.pattern("EBE")
		.pattern(" E ")
		.define('E', Tags.Items.ENDER_PEARLS)
		.define('B', Items.BOOK)
		.unlockedBy("has_ender_pearl", has(Tags.Items.ENDER_PEARLS))
		.save(output);
		//@formatter:on
	}

	public static final class Runner extends RecipeProvider.Runner {
		public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(output, lookupProvider);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider lookupProvider, RecipeOutput output) {
			return new RecipeGenerator(lookupProvider, output);
		}

		@Override
		public String getName() {
			return "XP Tome recipes";
		}
	}
}
