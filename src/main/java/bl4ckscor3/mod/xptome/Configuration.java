package bl4ckscor3.mod.xptome;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class Configuration {
	public static final ModConfigSpec CONFIG_SPEC;
	public static final Configuration CONFIG;
	private final IntValue maxXP;
	private final DoubleValue retrievalPercentage;
	private final BooleanValue retrieveXPOrbs;
	private final BooleanValue retriveUntilNextLevel;
	private final BooleanValue storeUntilPreviousLevel;

	static {
		Pair<Configuration, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Configuration::new);

		CONFIG_SPEC = specPair.getRight();
		CONFIG = specPair.getLeft();
	}

	Configuration(ModConfigSpec.Builder builder) {
		//@formatter:off
		builder.comment("This configuration will change the default components of the XP Tome item. Existing XP Tomes will change, unless they have differing components set.",
						"This configuration can be overwritten on individual XP Tome items by setting the corresponding components accordingly, for instance in a give command or a recipe.",
						"WARNING: Changing default components may lead to unintended visual side-effects in certain cases (like clients connecting to a server) - this config is mainly provided for ease-of-use in singleplayer environments.",
						"         Connecting to a server where the settings in this config file differ from the client's may cause visual inconsistencies and incorrect information to be displayed.",
						"         If you run a server, consider changing the recipe of the XP Tome instead of its default components.");
		builder.push("Default item components");
		maxXP = builder
				.comment("The maximum amount of XP points that the XP Tome can hold. The default value (1395) represents having 30 levels.")
				.defineInRange("max_xp", 1395, 1, Integer.MAX_VALUE);
		retrievalPercentage = builder
				.comment("The percentage of XP that the book will give back, as a sort of cost of using it.",
						"Example: If this config value is set to 0.75, and an XP Tome has 100 XP stored, attempting to retrieve these 100 XP will give back 75 XP.",
						"Note: This will not be 100% accurate, as Minecraft's XP does not use decimals.")
				.defineInRange("retrieval_percentage", 1.0D, 0.0D, 1.0D);
		retrieveXPOrbs = builder
				.comment("Setting this to true will remove XP from the book in XP orb form. This is useful if you want to use XP from the book for tools enchanted with Mending.")
				.define("retrieve_xp_orbs", false);
		retriveUntilNextLevel = builder
				.comment("Setting this to true will remove only as much XP from the book at a time as is needed for the player to reach their next level.")
				.define("retrieve_until_next_level", false);
		storeUntilPreviousLevel = builder
				.comment("Setting this to true will store only as much XP from the player's XP bar until reaching the previous level, meaning only one level at maximum will be added to the book's storage at a time.")
				.define("store_until_previous_level", false);
		builder.pop();
		//@formatter:on
	}

	public Item.Properties applyToDefaultComponents(Item.Properties properties) {
		properties.component(XPTome.MAXIMUM_XP, maxXP.getAsInt());
		properties.component(XPTome.RETRIEVAL_PERCENTAGE, retrievalPercentage.getAsDouble());

		if (retrieveXPOrbs.getAsBoolean())
			properties.component(XPTome.RETRIEVE_XP_ORBS, Unit.INSTANCE);

		if (retriveUntilNextLevel.getAsBoolean())
			properties.component(XPTome.RETRIEVE_UNTIL_NEXT_LEVEL, Unit.INSTANCE);

		if (storeUntilPreviousLevel.getAsBoolean())
			properties.component(XPTome.STORE_UNTIL_PREVIOUS_LEVEL, Unit.INSTANCE);

		return properties;
	}
}
