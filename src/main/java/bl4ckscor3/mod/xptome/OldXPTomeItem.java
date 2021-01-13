package bl4ckscor3.mod.xptome;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 *	@deprecated This is kept for legacy reasons. Use {@link XPTomeItem}
 */
@Deprecated
public class OldXPTomeItem extends Item
{
	public static final int MAX_STORAGE = 1395; //first 30 levels

	public OldXPTomeItem(Item.Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
		int xp = getXPStored(player.getHeldItem(hand));
		ItemStack newStack = new ItemStack(XPTome.XP_TOME.get());
		CompoundNBT tag = new CompoundNBT();

		tag.putInt("xp", xp);
		newStack.setTag(tag);

		if(world.isRemote) //only play the sound clientside
			player.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, 1.0F);

		return ActionResult.resultConsume(newStack);
	}

	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return getXPStored(stack) > 0;
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken)
	{
		return 0;
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {}

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
		tooltip.add(new TranslationTextComponent("xpbook.tooltip.3", getXPStored(stack), MAX_STORAGE).setStyle(XPTomeItem.TOOLTIP_STYLE));
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
