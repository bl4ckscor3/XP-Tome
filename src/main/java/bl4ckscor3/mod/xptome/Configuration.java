package bl4ckscor3.mod.xptome;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class Configuration
{
	public static final ForgeConfigSpec CONFIG_SPEC;
	public static final Configuration CONFIG;

	public final IntValue maxXP;

	static
	{
		Pair<Configuration,ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Configuration::new);

		CONFIG_SPEC = specPair.getRight();
		CONFIG = specPair.getLeft();
	}

	Configuration(ForgeConfigSpec.Builder builder)
	{
		maxXP = builder
				.comment("The maximum amount of XP points that the XP Tome can hold. The default value (1395) represents having 30 levels.")
				.defineInRange("max_xp", 1395, 1, Integer.MAX_VALUE);
	}
}
