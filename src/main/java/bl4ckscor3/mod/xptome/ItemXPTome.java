package bl4ckscor3.mod.xptome;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import openmods.utils.EnchantmentUtils;

public class ItemXPTome extends Item
{
	public static final String NAME = "xp_book";
	public static final int MAX_STORAGE = 1395; //first 30 levels

	public ItemXPTome()
	{
		setCreativeTab(CreativeTabs.MISC);
		setMaxDamage(MAX_STORAGE);
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		if(player.isSneaking())
		{
			int actuallyStored = addXP(stack, EnchantmentUtils.getPlayerXP(player)); //try to store all of the player's levels

			EnchantmentUtils.addPlayerXP(player, -actuallyStored);
		}
		else
		{
			EnchantmentUtils.addPlayerXP(player, getXPStored(stack));
			setStoredXP(stack, 0);
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return getXPStored(stack) > 0;
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
	{
		tooltip.add("Sneak + right-click to store as much XP as possible");
		tooltip.add("Right-click to retrieve all XP");
		tooltip.add(String.format("%s/%s XP stored", getXPStored(stack), MAX_STORAGE));
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
		stack.setItemDamage(MAX_STORAGE - amount);
	}

	/**
	 * Gets the amount of XP that the given stack has stored
	 * @param stack The stack to get the amount of stored XP from
	 * @return The amount of stored XP in the stack
	 */
	public int getXPStored(ItemStack stack)
	{
		return MAX_STORAGE - stack.getItemDamage(); //if the damage is 0, the book is full on xp
	}
}
