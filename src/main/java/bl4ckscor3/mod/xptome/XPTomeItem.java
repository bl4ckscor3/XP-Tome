package bl4ckscor3.mod.xptome;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import openmods.utils.EnchantmentUtils;

public class XPTomeItem extends Item
{
	public static final Style TOOLTIP_STYLE = Style.EMPTY.applyFormat(TextFormatting.GRAY);
	private static final ITextComponent TOOLTIP_1 = new TranslationTextComponent("xpbook.tooltip.1").setStyle(TOOLTIP_STYLE);
	private static final ITextComponent TOOLTIP_2 = new TranslationTextComponent("xpbook.tooltip.2").setStyle(TOOLTIP_STYLE);

	public XPTomeItem(Item.Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		int storedXP = getStoredXP(stack);

		if(player.isShiftKeyDown() && storedXP < Configuration.CONFIG.maxXP.get())
		{
			int xpToStore = 0;

			if(Configuration.CONFIG.storeUntilPreviousLevel.get())
			{
				int xpForCurrentLevel = EnchantmentUtils.getExperienceForLevel(player.experienceLevel);

				xpToStore = EnchantmentUtils.getPlayerXP(player) - xpForCurrentLevel;

				if(xpToStore == 0 && player.experienceLevel > 0) //player has exactly x > 0 levels (xp bar looks empty)
					xpToStore = xpForCurrentLevel - EnchantmentUtils.getExperienceForLevel(player.experienceLevel - 1);
			}
			else
				xpToStore = EnchantmentUtils.getPlayerXP(player);

			if(xpToStore == 0)
				return new ActionResult<>(ActionResultType.PASS, stack);

			int actuallyStored = addXP(stack, xpToStore); //store as much XP as possible

			if(actuallyStored > 0)
			{
				int previousLevel = player.experienceLevel;

				MinecraftForge.EVENT_BUS.post(new PlayerXpEvent.XpChange(player, -actuallyStored));
				EnchantmentUtils.addPlayerXP(player, -actuallyStored); //negative value removes xp

				if(previousLevel != player.experienceLevel)
					MinecraftForge.EVENT_BUS.post(new PlayerXpEvent.LevelChange(player, player.experienceLevel));
			}

			if(!world.isClientSide)
				world.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, (world.random.nextFloat() - world.random.nextFloat()) * 0.35F + 0.9F);

			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}
		else if(!player.isShiftKeyDown() && storedXP > 0)
		{
			if(Configuration.CONFIG.retriveUntilNextLevel.get())
			{
				int xpForPlayer = EnchantmentUtils.getExperienceForLevel(player.experienceLevel + 1) - EnchantmentUtils.getPlayerXP(player);
				//if retrievalPercentage is 75%, these 75% should be given to the player, but an extra 25% needs to be removed from the tome
				//using floor to be generous towards the player, removing slightly less xp than should be removed (can't be 100% accurate, because XP is saved as an int)
				int xpToRetrieve = (int)Math.floor(xpForPlayer / Configuration.CONFIG.retrievalPercentage.get());
				int actuallyRemoved = removeXP(stack, xpToRetrieve);

				//if the tome had less xp than the player should get, apply the XP loss to that value as well
				if(actuallyRemoved < xpForPlayer)
					xpForPlayer = (int)Math.floor(actuallyRemoved * Configuration.CONFIG.retrievalPercentage.get());

				addOrSpawnXPForPlayer(player, xpForPlayer);
			}
			else
			{
				//using ceil to be generous towards the player, adding slightly more xp than they should get (can't be 100% accurate, because XP is saved as an int)
				addOrSpawnXPForPlayer(player, (int)Math.ceil(storedXP * Configuration.CONFIG.retrievalPercentage.get()));
				setStoredXP(stack, 0);
			}

			if(!world.isClientSide && !Configuration.CONFIG.retrieveXPOrbs.get()) //picking up XP orbs creates a sound already, so only play a sound when XP is retrieved directly
			{
				float pitchMultiplier = player.experienceLevel > 30 ? 1.0F : player.experienceLevel / 30.0F;

				world.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundCategory.PLAYERS, pitchMultiplier * 0.75F, 1.0F);
			}

			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}

		return new ActionResult<>(ActionResultType.PASS, stack);
	}

	private void addOrSpawnXPForPlayer(PlayerEntity player, int amount)
	{
		if(Configuration.CONFIG.retrieveXPOrbs.get())
		{
			if(!player.level.isClientSide)
				player.level.addFreshEntity(new ExperienceOrbEntity(player.level, player.getX(), player.getY(), player.getZ(), amount));
		}
		else
		{
			int previousLevel = player.experienceLevel;

			MinecraftForge.EVENT_BUS.post(new PlayerXpEvent.XpChange(player, amount));
			EnchantmentUtils.addPlayerXP(player, amount);

			if(previousLevel != player.experienceLevel)
				MinecraftForge.EVENT_BUS.post(new PlayerXpEvent.LevelChange(player, player.experienceLevel));
		}
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		//returning 1 results in an empty bar. returning 0 results in a full bar
		//if there is more XP stored than MAX_STORAGE, the value will be negative, resulting in a longer than usual durability bar
		//having a lower bound of 0 ensures that the bar does not exceed its normal length
		return Math.max(0.0D, 1.0D - ((double)getStoredXP(stack) / (double)Configuration.CONFIG.maxXP.get()));
	}

	@Override
	public boolean isFoil(ItemStack stack)
	{
		return getStoredXP(stack) > 0;
	}

	@Override
	public boolean canBeDepleted()
	{
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book)
	{
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair)
	{
		return false;
	}

	@Override
	public boolean isRepairable(ItemStack stack)
	{
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		tooltip.add(TOOLTIP_1);
		tooltip.add(TOOLTIP_2);
		tooltip.add(new TranslationTextComponent("xpbook.tooltip.3", getStoredXP(stack), Configuration.CONFIG.maxXP.get()).setStyle(TOOLTIP_STYLE));
	}

	/**
	 * Tries to add the given amount of XP to the given stack. If that action would exceed the storage capacity, as much XP as possible will be stored.
	 * @param stack The stack to add XP to
	 * @param amount The amount of XP to add
	 * @return The amount XP that was added
	 */
	public int addXP(ItemStack stack, int amount)
	{
		if(amount <= 0) //can't add a negative amount of XP
			return 0;

		int stored = getStoredXP(stack);
		int maxStorage = Configuration.CONFIG.maxXP.get();

		if(stored >= maxStorage) //can't add XP to a full book
			return 0;

		if(stored + amount <= maxStorage)
		{
			setStoredXP(stack, stored + amount);
			return amount;
		}
		else
		{
			setStoredXP(stack, maxStorage);
			return maxStorage - stored;
		}
	}

	/**
	 * Tries to remove the given amount of XP from the given stack. If that action would result in a negative XP value, the book will end up with 0 stored XP.
	 * @param stack The stack to remove XP from
	 * @param amount The amount of XP to remove
	 * @return The amount XP that was removed
	 */
	public int removeXP(ItemStack stack, int amount)
	{
		if(amount <= 0) //can't remove a negative amount of XP
			return 0;

		int stored = getStoredXP(stack);

		if(stored <= 0) //can't remove XP from an empty book
			return 0;

		if(stored >= amount)
		{
			setStoredXP(stack, stored - amount);
			return amount;
		}
		else
		{
			setStoredXP(stack, 0);
			return stored;
		}
	}

	/**
	 * Sets the amount of XP that is stored in the given stack. Does not respect maximum possible storage
	 * @param stack The stack to set the amount of stored XP of
	 * @param amount The amount of XP to set the storage to
	 */
	public void setStoredXP(ItemStack stack, int amount)
	{
		stack.getOrCreateTag().putInt("xp", amount);
	}

	/**
	 * Gets the amount of XP that the given stack has stored
	 * @param stack The stack to get the amount of stored XP from
	 * @return The amount of stored XP in the stack
	 */
	public int getStoredXP(ItemStack stack)
	{
		return stack.getOrCreateTag().getInt("xp");
	}
}
