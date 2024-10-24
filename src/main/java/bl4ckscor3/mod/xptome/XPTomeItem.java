package bl4ckscor3.mod.xptome;

import java.util.List;

import bl4ckscor3.mod.xptome.openmods.utils.EnchantmentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

public class XPTomeItem extends Item {
	public static final int DEFAULT_MAX_XP = 1395;
	private static final Component TOOLTIP_STORE_MAX = Component.translatable("xpbook.tooltip.store.max").withStyle(ChatFormatting.GRAY);
	private static final Component TOOLTIP_STORE_PREVIOUS = Component.translatable("xpbook.tooltip.store.previous").withStyle(ChatFormatting.GRAY);
	private static final Component TOOLTIP_RETRIEVE_MAX = Component.translatable("xpbook.tooltip.retrieve.max").withStyle(ChatFormatting.GRAY);
	private static final Component TOOLTIP_RETRIEVE_NEXT = Component.translatable("xpbook.tooltip.retrieve.next").withStyle(ChatFormatting.GRAY);
	private static final Component TOOLTIP_RETRIEVES_AS_ORBS = Component.translatable("xpbook.tooltip.retrieve.orb").withStyle(ChatFormatting.AQUA);

	public XPTomeItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		int storedXP = getStoredXP(stack);

		if (stack.getCount() > 1)
			return InteractionResult.PASS;

		if (player.isShiftKeyDown() && storedXP < getMaxXP(stack)) {
			int xpToStore = 0;

			if (stack.has(XPTome.STORE_UNTIL_PREVIOUS_LEVEL)) {
				int xpForCurrentLevel = EnchantmentUtils.getExperienceForLevel(player.experienceLevel);

				xpToStore = EnchantmentUtils.getPlayerXP(player) - xpForCurrentLevel;

				if (xpToStore == 0 && player.experienceLevel > 0) //player has exactly x > 0 levels (xp bar looks empty)
					xpToStore = xpForCurrentLevel - EnchantmentUtils.getExperienceForLevel(player.experienceLevel - 1);
			}
			else
				xpToStore = EnchantmentUtils.getPlayerXP(player);

			if (xpToStore == 0)
				return InteractionResult.PASS;

			int actuallyStored = addXP(stack, xpToStore); //store as much XP as possible

			if (actuallyStored > 0) {
				int previousLevel = player.experienceLevel;

				NeoForge.EVENT_BUS.post(new PlayerXpEvent.XpChange(player, -actuallyStored));
				EnchantmentUtils.addPlayerXP(player, -actuallyStored); //negative value removes xp

				if (previousLevel != player.experienceLevel)
					NeoForge.EVENT_BUS.post(new PlayerXpEvent.LevelChange(player, player.experienceLevel));
			}

			if (!level.isClientSide)
				level.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, (level.random.nextFloat() - level.random.nextFloat()) * 0.35F + 0.9F);

			return InteractionResult.SUCCESS_SERVER;
		}
		else if (!player.isShiftKeyDown() && storedXP > 0) {
			boolean asOrbs = stack.has(XPTome.RETRIEVE_XP_ORBS);

			if (stack.has(XPTome.RETRIEVE_UNTIL_NEXT_LEVEL)) {
				int xpForPlayer = EnchantmentUtils.getExperienceForLevel(player.experienceLevel + 1) - EnchantmentUtils.getPlayerXP(player);
				//if retrievalPercentage is 75%, these 75% should be given to the player, but an extra 25% needs to be removed from the tome
				//using floor to be generous towards the player, removing slightly less xp than should be removed (can't be 100% accurate, because XP is saved as an int)
				double retrievalPercentage = getRetrievalPercentage(stack);
				int xpToRetrieve = retrievalPercentage == 0.0D ? 0 : (int) Math.floor(xpForPlayer / retrievalPercentage);
				int actuallyRemoved = removeXP(stack, xpToRetrieve);

				//if the tome had less xp than the player should get, apply the XP loss to that value as well
				if (actuallyRemoved < xpForPlayer)
					xpForPlayer = (int) Math.floor(actuallyRemoved * retrievalPercentage);

				addOrSpawnXPForPlayer(player, xpForPlayer, asOrbs);
			}
			else {
				//using ceil to be generous towards the player, adding slightly more xp than they should get (can't be 100% accurate, because XP is saved as an int)
				addOrSpawnXPForPlayer(player, (int) Math.ceil(storedXP * getRetrievalPercentage(stack)), asOrbs);
				setStoredXP(stack, 0);
			}

			if (!level.isClientSide && !asOrbs) { //picking up XP orbs creates a sound already, so only play a sound when XP is retrieved directly
				float pitchMultiplier = player.experienceLevel > 30 ? 1.0F : player.experienceLevel / 30.0F;

				level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, pitchMultiplier * 0.75F, 1.0F);
			}

			return InteractionResult.SUCCESS_SERVER;
		}

		return InteractionResult.PASS;
	}

	private void addOrSpawnXPForPlayer(Player player, int amount, boolean asOrbs) {
		if (asOrbs) {
			if (!player.level().isClientSide)
				player.level().addFreshEntity(new ExperienceOrb(player.level(), player.getX(), player.getY(), player.getZ(), amount));
		}
		else {
			int previousLevel = player.experienceLevel;

			NeoForge.EVENT_BUS.post(new PlayerXpEvent.XpChange(player, amount));
			EnchantmentUtils.addPlayerXP(player, amount);

			if (previousLevel != player.experienceLevel)
				NeoForge.EVENT_BUS.post(new PlayerXpEvent.LevelChange(player, player.experienceLevel));
		}
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		//returning 1 results in an empty bar. returning 0 results in a full bar
		//if there is more XP stored than MAX_STORAGE, the value will be negative, resulting in a longer than usual durability bar
		//having a lower bound of 0 ensures that the bar does not exceed its normal length
		return (int) Math.max(0.0D, MAX_BAR_WIDTH * ((double) getStoredXP(stack) / (double) getMaxXP(stack)));
	}

	@Override
	public int getBarColor(ItemStack stack) {
		float maxXP = getMaxXP(stack);
		float f = Math.max(0.0F, getStoredXP(stack) / maxXP);

		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return getStoredXP(stack) > 0;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	@Override
	public boolean isRepairable(ItemStack stack) {
		return false;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
		if (stack.has(XPTome.STORE_UNTIL_PREVIOUS_LEVEL))
			tooltip.add(TOOLTIP_STORE_PREVIOUS);
		else
			tooltip.add(TOOLTIP_STORE_MAX);

		if (stack.has(XPTome.RETRIEVE_UNTIL_NEXT_LEVEL))
			tooltip.add(TOOLTIP_RETRIEVE_NEXT);
		else
			tooltip.add(TOOLTIP_RETRIEVE_MAX);

		if (stack.has(XPTome.RETRIEVE_XP_ORBS))
			tooltip.add(TOOLTIP_RETRIEVES_AS_ORBS);

		int storedXP = getStoredXP(stack);
		int maxXP = getMaxXP(stack);
		double fillLevel = storedXP / (double) maxXP;
		ChatFormatting color = ChatFormatting.GREEN;

		if (fillLevel >= 1.0D)
			color = ChatFormatting.RED;
		else if (fillLevel >= 0.9D)
			color = ChatFormatting.YELLOW;

		tooltip.add(Component.translatable("xpbook.tooltip.stored_xp", storedXP, maxXP).withStyle(color));
	}

	/**
	 * Tries to add the given amount of XP to the given stack. If that action would exceed the storage capacity, as much XP as
	 * possible will be stored.
	 *
	 * @param stack The stack to add XP to
	 * @param amount The amount of XP to add
	 * @return The amount XP that was added
	 */
	public static int addXP(ItemStack stack, int amount) {
		if (amount <= 0) //can't add a negative amount of XP
			return 0;

		int stored = getStoredXP(stack);
		int maxStorage = getMaxXP(stack);

		if (stored >= maxStorage) //can't add XP to a full book
			return 0;

		if (stored + amount <= maxStorage) {
			setStoredXP(stack, stored + amount);
			return amount;
		}
		else {
			setStoredXP(stack, maxStorage);
			return maxStorage - stored;
		}
	}

	/**
	 * Tries to remove the given amount of XP from the given stack. If that action would result in a negative XP value, the book
	 * will end up with 0 stored XP.
	 *
	 * @param stack The stack to remove XP from
	 * @param amount The amount of XP to remove
	 * @return The amount XP that was removed
	 */
	public static int removeXP(ItemStack stack, int amount) {
		if (amount <= 0) //can't remove a negative amount of XP
			return 0;

		int stored = getStoredXP(stack);

		if (stored <= 0) //can't remove XP from an empty book
			return 0;

		if (stored >= amount) {
			setStoredXP(stack, stored - amount);
			return amount;
		}
		else {
			setStoredXP(stack, 0);
			return stored;
		}
	}

	/**
	 * Sets the amount of XP that is stored in the given stack. Does not respect maximum possible storage
	 *
	 * @param stack The stack to set the amount of stored XP of
	 * @param amount The amount of XP to set the storage to
	 */
	public static void setStoredXP(ItemStack stack, int amount) {
		stack.set(XPTome.STORED_XP, amount);
	}

	/**
	 * Gets the amount of XP that the given stack has stored
	 *
	 * @param stack The stack to get the amount of stored XP from
	 * @return The amount of stored XP in the stack
	 */
	public static int getStoredXP(ItemStack stack) {
		return stack.getOrDefault(XPTome.STORED_XP, 0);
	}

	/**
	 * Gets the maximum amount of XP that the given stack can store
	 *
	 * @param stack The stack to get the maximum amount of XP from
	 * @return The maximum amount of XP the stack can store
	 */
	public static int getMaxXP(ItemStack stack) {
		return stack.getOrDefault(XPTome.MAXIMUM_XP, DEFAULT_MAX_XP);
	}

	/**
	 * Gets the percentage of XP that the given stack will give back to the player
	 *
	 * @param stack The stack to get the retrieval percentage from
	 * @return The percentage of XP the stack will give back
	 */
	public static double getRetrievalPercentage(ItemStack stack) {
		return stack.getOrDefault(XPTome.RETRIEVAL_PERCENTAGE, 1.0D);
	}
}
