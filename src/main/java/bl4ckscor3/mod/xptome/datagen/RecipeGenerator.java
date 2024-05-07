package bl4ckscor3.mod.xptome.datagen;

import java.util.concurrent.CompletableFuture;

import bl4ckscor3.mod.xptome.XPTome;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class RecipeGenerator extends RecipeProvider {
	public RecipeGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
	}

	@Override
	protected final void buildRecipes(RecipeOutput recipeOutput) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, XPTome.XP_TOME)
		.pattern(" E ")
		.pattern("EBE")
		.pattern(" E ")
		.define('E', Tags.Items.ENDER_PEARLS)
		.define('B', Items.BOOK)
		.unlockedBy("has_ender_pearl", has(Tags.Items.ENDER_PEARLS))
		.save(recipeOutput);
		//@formatter:on
	}
}
