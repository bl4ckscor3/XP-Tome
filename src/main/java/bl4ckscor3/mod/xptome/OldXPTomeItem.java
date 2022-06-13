package bl4ckscor3.mod.xptome;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
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
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
	{
		int xp = getXPStored(player.getItemInHand(hand));
		ItemStack newStack = new ItemStack(XPTome.XP_TOME.get());
		CompoundTag tag = new CompoundTag();

		tag.putInt("xp", xp);
		newStack.setTag(tag);

		if(world.isClientSide) //only play the sound clientside
			player.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 1.0F);

		return InteractionResultHolder.consume(newStack);
	}

	@Override
	public boolean isFoil(ItemStack stack)
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
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
	{
		tooltip.add(Component.translatable("xpbook.tooltip.3", getXPStored(stack), MAX_STORAGE).setStyle(XPTomeItem.TOOLTIP_STYLE));
	}

	/**
	 * Gets the amount of XP that the given stack has stored
	 * @param stack The stack to get the amount of stored XP from
	 * @return The amount of stored XP in the stack
	 */
	public int getXPStored(ItemStack stack)
	{
		return MAX_STORAGE - stack.getDamageValue(); //if the damage is 0, the book is full on xp
	}
}
