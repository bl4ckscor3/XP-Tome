package bl4ckscor3.mod.xptome;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class Configuration {
	public static final ModConfigSpec CONFIG_SPEC;
	public static final Configuration CONFIG;
	public final IntValue maxXP;
	public final DoubleValue retrievalPercentage;
	public final BooleanValue retriveUntilNextLevel;
	public final BooleanValue retrieveXPOrbs;
	public final BooleanValue storeUntilPreviousLevel;

	static {
		Pair<Configuration, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Configuration::new);

		CONFIG_SPEC = specPair.getRight();
		CONFIG = specPair.getLeft();
	}

	Configuration(ModConfigSpec.Builder builder) {
		//@formatter:off
		maxXP = builder
				.comment("The maximum amount of XP points that the XP Tome can hold. The default value (1395) represents having 30 levels.")
				.defineInRange("max_xp", 1395, 1, Integer.MAX_VALUE);
		retrievalPercentage = builder
				.comment("The percentage of XP that the book will give back, as a sort of cost of using it.",
						"Example: If this config value is set to 0.75, and an XP Tome has 100 XP stored, attempting to retrieve these 100 XP will give back 75 XP.",
						"Note: This will not be 100% accurate, as Minecraft's XP does not use decimals.")
				.defineInRange("retrieval_percentage", 1.0D, 0.0D, 1.0D);
		retriveUntilNextLevel = builder
				.comment("Setting this to true will remove only as much XP from the book at a time as is needed for the player to reach their next level.")
				.define("retrieve_until_next_level", false);
		retrieveXPOrbs = builder
				.comment("Setting this to true will remove XP from the book in XP orb form. This is useful if you want to use XP from the book for tools enchanted with Mending.")
				.define("retrieve_xp_orbs", false);
		storeUntilPreviousLevel = builder
				.comment("Setting this to true will store only as much XP from the player's XP bar until reaching the previous level, meaning only one level at maximum will be added to the book's storage at a time.")
				.define("store_until_previous_level", false);
		//@formatter:on
	}
}
