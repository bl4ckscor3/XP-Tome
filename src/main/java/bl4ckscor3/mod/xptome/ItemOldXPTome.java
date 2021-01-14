package bl4ckscor3.mod.xptome;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *	@deprecated This is kept for legacy reasons. Use {@link XPTomeItem}
 */
@Deprecated
public class ItemOldXPTome extends Item
{
	public static final String NAME = "xp_book";
	public static final int MAX_STORAGE = 1395; //first 30 levels

	public ItemOldXPTome()
	{
		setMaxDamage(MAX_STORAGE);
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		int xp = getXPStored(player.getHeldItem(hand));
		ItemStack newStack = new ItemStack(XPTome.XP_TOME);
		NBTTagCompound tag = new NBTTagCompound();

		tag.setInteger("xp", xp);
		newStack.setTagCompound(tag);

		if(world.isRemote) //only play the sound clientside
			player.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, 1.0F);

		return new ActionResult<>(EnumActionResult.SUCCESS, newStack);
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return false;
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
	public boolean isRepairable()
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
	{
		tooltip.add(I18n.format("xpbook.tooltip.3", getXPStored(stack), MAX_STORAGE));
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
