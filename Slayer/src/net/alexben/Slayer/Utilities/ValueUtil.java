package net.alexben.Slayer.Utilities;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * Handles values for all mobs and items in the game.
 */
public class ValueUtil
{
	/**
	 * Returns an experience value for the <code>entity</code> passed in.
	 * 
	 * @return Integer
	 */
	public static int getEntityValue(Entity entity)
	{
		if(entity == null) return 0;

		switch(entity.getType())
		{
			case BAT:
				return MiscUtil.getValue(entity.getType().getName());
			case BLAZE:
				return MiscUtil.getValue(entity.getType().getName());
			case CHICKEN:
				return MiscUtil.getValue(entity.getType().getName());
			case CAVE_SPIDER:
				return MiscUtil.getValue(entity.getType().getName());
			case COW:
				return MiscUtil.getValue(entity.getType().getName());
			case CREEPER:
				return MiscUtil.getValue(entity.getType().getName());
			case ENDER_CRYSTAL:
				return MiscUtil.getValue(entity.getType().getName());
			case ENDER_DRAGON:
				return MiscUtil.getValue(entity.getType().getName());
			case ENDERMAN:
				return MiscUtil.getValue(entity.getType().getName());
			case GHAST:
				return MiscUtil.getValue(entity.getType().getName());
			case GIANT:
				return MiscUtil.getValue(entity.getType().getName());
			case IRON_GOLEM:
				return MiscUtil.getValue(entity.getType().getName());
			case PLAYER:
				return MiscUtil.getValue(entity.getType().getName());
			case MAGMA_CUBE:
				return MiscUtil.getValue(entity.getType().getName());
			case MUSHROOM_COW:
				return MiscUtil.getValue(entity.getType().getName());
			case OCELOT:
				return MiscUtil.getValue(entity.getType().getName());
			case PIG_ZOMBIE:
				return MiscUtil.getValue(entity.getType().getName());
			case PIG:
				return MiscUtil.getValue(entity.getType().getName());
			case SHEEP:
				return MiscUtil.getValue(entity.getType().getName());
			case SILVERFISH:
				return MiscUtil.getValue(entity.getType().getName());
			case SKELETON:
				return MiscUtil.getValue(entity.getType().getName());
			case SLIME:
				return MiscUtil.getValue(entity.getType().getName());
			case SNOWMAN:
				return MiscUtil.getValue(entity.getType().getName());
			case SPIDER:
				return MiscUtil.getValue(entity.getType().getName());
			case SQUID:
				return MiscUtil.getValue(entity.getType().getName());
			case VILLAGER:
				return MiscUtil.getValue(entity.getType().getName());
			case WITCH:
				return MiscUtil.getValue(entity.getType().getName());
			case WITHER_SKULL:
				return MiscUtil.getValue(entity.getType().getName());
			case WITHER:
				return MiscUtil.getValue(entity.getType().getName());
			case WOLF:
				return MiscUtil.getValue(entity.getType().getName());
			case ZOMBIE:
				return MiscUtil.getValue(entity.getType().getName());

			default:
				return 0;
		}
	}

	/**
	 * Returns an experience value for the <code>item</code> passed in.
	 * 
	 * @return Integer
	 */
	public static int getItemValue(ItemStack item)
	{
		if(item == null) return 0;

		switch(item.getType())
		{
			case APPLE:
				return MiscUtil.getValue(item.getType().name());
			case ACTIVATOR_RAIL:
				return MiscUtil.getValue(item.getType().name());
			case ANVIL:
				return MiscUtil.getValue(item.getType().name());
			case ARROW:
				return MiscUtil.getValue(item.getType().name());
			case BAKED_POTATO:
				return MiscUtil.getValue(item.getType().name());
			case BEACON:
				return MiscUtil.getValue(item.getType().name());
			case BED:
				return MiscUtil.getValue(item.getType().name());
			case BEDROCK:
				return MiscUtil.getValue(item.getType().name());
			case BIRCH_WOOD_STAIRS:
				return MiscUtil.getValue(item.getType().name());
			case BLAZE_POWDER:
				return MiscUtil.getValue(item.getType().name());
			case BLAZE_ROD:
				return MiscUtil.getValue(item.getType().name());
			case BOAT:
				return MiscUtil.getValue(item.getType().name());
			case BONE:
				return MiscUtil.getValue(item.getType().name());
			case BOOK:
				return MiscUtil.getValue(item.getType().name());
			case BOOK_AND_QUILL:
				return MiscUtil.getValue(item.getType().name());
			case BOOKSHELF:
				return MiscUtil.getValue(item.getType().name());
			case BOW:
				return MiscUtil.getValue(item.getType().name());
			case BOWL:
				return MiscUtil.getValue(item.getType().name());
			case BREAD:
				return MiscUtil.getValue(item.getType().name());
			case BREWING_STAND:
				return MiscUtil.getValue(item.getType().name());
			case BRICK:
				return MiscUtil.getValue(item.getType().name());
			case BRICK_STAIRS:
				return MiscUtil.getValue(item.getType().name());
			case BROWN_MUSHROOM:
				return MiscUtil.getValue(item.getType().name());
			case BUCKET:
				return MiscUtil.getValue(item.getType().name());
			case CACTUS:
				return MiscUtil.getValue(item.getType().name());
			case CAKE:
				return MiscUtil.getValue(item.getType().name());
			case CARROT:
				return MiscUtil.getValue(item.getType().name());
			case CARROT_STICK:
				return MiscUtil.getValue(item.getType().name());
			case CAULDRON:
				return MiscUtil.getValue(item.getType().name());
			case CHAINMAIL_BOOTS:
				return MiscUtil.getValue(item.getType().name());
			case CHAINMAIL_CHESTPLATE:
				return MiscUtil.getValue(item.getType().name());
			case CHAINMAIL_HELMET:
				return MiscUtil.getValue(item.getType().name());
			case CHAINMAIL_LEGGINGS:
				return MiscUtil.getValue(item.getType().name());
			case CHEST:
				return MiscUtil.getValue(item.getType().name());
			case CLAY:
				return MiscUtil.getValue(item.getType().name());
			case CLAY_BALL:
				return MiscUtil.getValue(item.getType().name());
			case CLAY_BRICK:
				return MiscUtil.getValue(item.getType().name());
			case COAL:
				return MiscUtil.getValue(item.getType().name());
			case COAL_ORE:
				return MiscUtil.getValue(item.getType().name());
			case COBBLE_WALL:
				return MiscUtil.getValue(item.getType().name());
			case COBBLESTONE:
				return MiscUtil.getValue(item.getType().name());
			case COBBLESTONE_STAIRS:
				return MiscUtil.getValue(item.getType().name());
			case COCOA:
				return MiscUtil.getValue(item.getType().name());
			case COMMAND:
				return MiscUtil.getValue(item.getType().name());
			case COMPASS:
				return MiscUtil.getValue(item.getType().name());
			case COOKIE:
				return MiscUtil.getValue(item.getType().name());
			case CROPS:
				return MiscUtil.getValue(item.getType().name());
			case COOKED_BEEF:
				return MiscUtil.getValue(item.getType().name());
			case COOKED_CHICKEN:
				return MiscUtil.getValue(item.getType().name());
			case COOKED_FISH:
				return MiscUtil.getValue(item.getType().name());
			case DAYLIGHT_DETECTOR:
				return MiscUtil.getValue(item.getType().name());
			case DEAD_BUSH:
				return MiscUtil.getValue(item.getType().name());
			case DETECTOR_RAIL:
				return MiscUtil.getValue(item.getType().name());
			case DIAMOND:
				return MiscUtil.getValue(item.getType().name());
			case DIAMOND_AXE:
				return MiscUtil.getValue(item.getType().name());
			case DIAMOND_BLOCK:
				return MiscUtil.getValue(item.getType().name());
			case DIAMOND_BOOTS:
				return MiscUtil.getValue(item.getType().name());
			case DIAMOND_CHESTPLATE:
				return MiscUtil.getValue(item.getType().name());
			case DIAMOND_HELMET:
				return MiscUtil.getValue(item.getType().name());
			case DIAMOND_LEGGINGS:
				return MiscUtil.getValue(item.getType().name());
			case DIAMOND_HOE:
				return MiscUtil.getValue(item.getType().name());
			case DIAMOND_SWORD:
				return MiscUtil.getValue(item.getType().name());
			case DIODE:
				return MiscUtil.getValue(item.getType().name());
			case DIRT:
				return MiscUtil.getValue(item.getType().name());
			case DISPENSER:
				return MiscUtil.getValue(item.getType().name());
			case DOUBLE_STEP:
				return MiscUtil.getValue(item.getType().name());

				// Unimplemented as of now

			default:
				return 0;
		}
	}
}
