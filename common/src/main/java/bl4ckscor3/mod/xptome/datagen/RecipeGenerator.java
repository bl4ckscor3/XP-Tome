package bl4ckscor3.mod.xptome.datagen;

import bl4ckscor3.mod.xptome.XPTome;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeGenerator extends RecipeProvider {
	private static final TagKey<Item> ENDER_PEARLS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ender_pearls"));
	private final HolderGetter<Item> items;

	public RecipeGenerator(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
		super(recipeOutput, advancementOutput);
		items = recipeOutput.lookup(Registries.ITEM);
	}

	@Override
	public final void buildRecipes() {
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, XPTome.XP_TOME.get())
			.pattern(" E ")
			.pattern("EBE")
			.pattern(" E ")
			.define('E', ENDER_PEARLS)
			.define('B', Items.BOOK)
			.unlockedBy("has_ender_pearl", has(ENDER_PEARLS))
			.save(output);
	}
}
