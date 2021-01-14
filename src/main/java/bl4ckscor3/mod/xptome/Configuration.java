package bl4ckscor3.mod.xptome;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;

@Config(modid=XPTome.MODID, name="xptome")
public class Configuration
{
	@Name("max_xp")
	@Comment("The maximum amount of XP points that the XP Tome can hold. The default value (1395) represents having 30 levels.")
	@RangeInt(min=1)
	public static int maxXP = 1395;
}
