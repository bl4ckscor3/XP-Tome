package bl4ckscor3.mod.xptome;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;

@Config(modid=XPTome.MODID, name="xptome")
public class Configuration
{
	@Name("max_xp")
	@Comment("The maximum amount of XP points that the XP Tome can hold. The default value (1395) represents having 30 levels.")
	@RangeInt(min=1)
	public static int maxXP = 1395;
	@Name("retrieval_percentage")
	@Comment({
		"The percentage of XP that the book will give back, as a sort of cost of using it.",
		"Example: If this config value is set to 0.75, and an XP Tome has 100 XP stored, attempting to retrieve these 100 XP will give back 75 XP."
	})
	@RangeDouble(min=0.0D, max=1.0D)
	public static double retrievalPercentage = 1.0D;
	@Name("store_until_previous_level")
	@Comment("Setting this to true will store only as much XP from the player's XP bar until reaching the previous level, meaning only one level at maximum will be added to the book's storage at a time.")
	public static boolean storeUntilPreviousLevel = false;
}
