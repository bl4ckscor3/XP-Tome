package bl4ckscor3.mod.xptome;

import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

/**
 * @deprecated This is kept for legacy reasons. Use {@link XPTomeItem}
 */
@Deprecated
public class OldXPTomeItem extends Item {
	public static final int MAX_STORAGE = 1395; //first 30 levels

	public OldXPTomeItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level world, Player player, InteractionHand hand) {
		int xp = getXPStored(player.getItemInHand(hand));
		ItemStack newStack = new ItemStack(XPTome.XP_TOME.get());

		newStack.set(XPTome.STORED_XP.get(), xp);

		if (world.isClientSide()) //only play the sound clientside
			player.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 1.0F);

		return InteractionResult.CONSUME.heldItemTransformedTo(newStack);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return getXPStored(stack) > 0;
	}

	//NeoForge only
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
		return 0;
	}

	//NeoForge only
	public void setDamage(ItemStack stack, int damage) {}

	//NeoForge only
	public boolean isCombineRepairable(ItemStack stack) {
		return false;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		tooltipAdder.accept(Component.translatable("xpbook.tooltip.3", getXPStored(stack), MAX_STORAGE).withStyle(ChatFormatting.GRAY));
	}

	/**
	 * Gets the amount of XP that the given stack has stored
	 *
	 * @param stack The stack to get the amount of stored XP from
	 * @return The amount of stored XP in the stack
	 */
	public int getXPStored(ItemStack stack) {
		return MAX_STORAGE - stack.getDamageValue(); //if the damage is 0, the book is full on xp
	}
}
