package bl4ckscor3.mod.xptome;

import java.util.List;
import java.util.Random;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
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
import openmods.utils.EnchantmentUtils;

public class OldXPTomeItem extends Item
{
	public static final String NAME = "xp_book";
	public static final int MAX_STORAGE = 1395; //first 30 levels
	private static final Style TOOLTIP_STYLE = Style.EMPTY.applyFormatting(TextFormatting.GRAY);
	private static final ITextComponent TOOLTIP_1 = new TranslationTextComponent("xpbook.tooltip.1").setStyle(TOOLTIP_STYLE);
	private static final ITextComponent TOOLTIP_2 = new TranslationTextComponent("xpbook.tooltip.2").setStyle(TOOLTIP_STYLE);
	private final Random random = new Random();

	public OldXPTomeItem()
	{
		super(new Item.Properties().maxDamage(MAX_STORAGE).group(ItemGroup.MISC));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		if(player.isCrouching() && getXPStored(stack) != MAX_STORAGE)
		{
			int playerXP = EnchantmentUtils.getPlayerXP(player);

			if(playerXP == 0)
				return new ActionResult<>(ActionResultType.PASS, stack);

			int actuallyStored = addXP(stack, playerXP); //try to store all of the player's levels

			EnchantmentUtils.addPlayerXP(player, -actuallyStored);

			if(!world.isRemote)
				world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, (random.nextFloat() - random.nextFloat()) * 0.35F + 0.9F);

			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}
		else if(!player.isCrouching() && getXPStored(stack) != 0)
		{
			EnchantmentUtils.addPlayerXP(player, getXPStored(stack));
			setStoredXP(stack, 0);

			if(!world.isRemote)
			{
				float pitchMultiplier = player.experienceLevel > 30 ? 1.0F : player.experienceLevel / 30.0F;

				world.playSound(null, player.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, pitchMultiplier * 0.75F, 1.0F);
			}

			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}

		return new ActionResult<>(ActionResultType.PASS, stack);
	}

	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return getXPStored(stack) > 0;
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
	public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		tooltip.add(TOOLTIP_1);
		tooltip.add(TOOLTIP_2);
		tooltip.add(new TranslationTextComponent("xpbook.tooltip.3", getXPStored(stack), MAX_STORAGE).setStyle(TOOLTIP_STYLE));
	}

	/**
	 * Adds the given amount of XP to the given stack. If that action would exceed the storage capacity, as much XP as possible will be stored.
	 * @param stack The stack to add XP to
	 * @param amount The amount of XP to add
	 * @return The amount XP that was added
	 */
	public int addXP(ItemStack stack, int amount)
	{
		int stored = getXPStored(stack);

		if(stored + amount > MAX_STORAGE)
		{
			setStoredXP(stack, MAX_STORAGE);
			return MAX_STORAGE - stored;
		}
		else
		{
			setStoredXP(stack, stored + amount);
			return amount;
		}
	}

	/**
	 * Sets the amount of XP that is stored in the given stack
	 * @param stack The stack to set the amount of stored XP of
	 * @param amount The amount of XP to set the storage to
	 */
	public void setStoredXP(ItemStack stack, int amount)
	{
		stack.setDamage(MAX_STORAGE - amount);
	}

	/**
	 * Gets the amount of XP that the given stack has stored
	 * @param stack The stack to get the amount of stored XP from
	 * @return The amount of stored XP in the stack
	 */
	public int getXPStored(ItemStack stack)
	{
		return MAX_STORAGE - stack.getDamage(); //if the damage is 0, the book is full on xp
	}
}
