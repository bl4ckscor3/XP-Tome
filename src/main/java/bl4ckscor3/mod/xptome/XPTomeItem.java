package bl4ckscor3.mod.xptome;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
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
import openmods.utils.EnchantmentUtils;

public class XPTomeItem extends Item
{
	public static final Style TOOLTIP_STYLE = Style.EMPTY.applyFormatting(TextFormatting.GRAY);
	private static final ITextComponent TOOLTIP_1 = new TranslationTextComponent("xpbook.tooltip.1").setStyle(TOOLTIP_STYLE);
	private static final ITextComponent TOOLTIP_2 = new TranslationTextComponent("xpbook.tooltip.2").setStyle(TOOLTIP_STYLE);

	public XPTomeItem(Item.Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		int storedXP = getXPStored(stack);

		if(player.isSneaking() && storedXP < Configuration.CONFIG.maxXP.get())
		{
			int xpToStore = 0;

			if(Configuration.CONFIG.storeUntilPreviousLevel.get())
			{
				int xpForCurrentLevel = EnchantmentUtils.getExperienceForLevel(player.experienceLevel);

				xpToStore = EnchantmentUtils.getPlayerXP(player) - xpForCurrentLevel;

				if(xpToStore == 0 && player.experienceLevel > 0) //player has exactly x levels (xp bar looks empty)
					xpToStore = xpForCurrentLevel - EnchantmentUtils.getExperienceForLevel(player.experienceLevel - 1);
			}
			else
				xpToStore = EnchantmentUtils.getPlayerXP(player);

			if(xpToStore == 0)
				return new ActionResult<>(ActionResultType.PASS, stack);

			int actuallyStored = addXP(stack, xpToStore); //store as much XP as possible

			if(actuallyStored > 0)
				EnchantmentUtils.addPlayerXP(player, -actuallyStored);

			if(!world.isRemote)
				world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.35F + 0.9F);

			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}
		else if(!player.isSneaking() && storedXP > 0)
		{
			EnchantmentUtils.addPlayerXP(player, (int)Math.ceil(storedXP * Configuration.CONFIG.retrievalPercentage.get()));
			setStoredXP(stack, 0);

			if(!world.isRemote)
			{
				float pitchMultiplier = player.experienceLevel > 30 ? 1.0F : player.experienceLevel / 30.0F;

				world.playSound(null, player.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.1F, pitchMultiplier * 0.75F);
			}

			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}

		return new ActionResult<>(ActionResultType.PASS, stack);
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
		return Math.max(0.0D, 1.0D - ((double)getXPStored(stack) / (double)Configuration.CONFIG.maxXP.get()));
	}

	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return getXPStored(stack) > 0;
	}

	@Override
	public boolean isDamageable()
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
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
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
	public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		tooltip.add(TOOLTIP_1);
		tooltip.add(TOOLTIP_2);
		tooltip.add(new TranslationTextComponent("xpbook.tooltip.3", getXPStored(stack), Configuration.CONFIG.maxXP.get()).setStyle(TOOLTIP_STYLE));
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

		int stored = getXPStored(stack);
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

		int stored = getXPStored(stack);

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
	public int getXPStored(ItemStack stack)
	{
		return stack.getOrCreateTag().getInt("xp");
	}
}
