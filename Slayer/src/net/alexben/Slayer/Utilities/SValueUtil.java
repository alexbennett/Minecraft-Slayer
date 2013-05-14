/*
 * Copyright (c) 2013 Alex Bennett
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.alexben.Slayer.Utilities;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * Handles values for all mobs and items in the game.
 */
public class SValueUtil
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
				return SMiscUtil.getValue(entity.getType().getName());
			case BLAZE:
				return SMiscUtil.getValue(entity.getType().getName());
			case CHICKEN:
				return SMiscUtil.getValue(entity.getType().getName());
			case CAVE_SPIDER:
				return SMiscUtil.getValue(entity.getType().getName());
			case COW:
				return SMiscUtil.getValue(entity.getType().getName());
			case CREEPER:
				return SMiscUtil.getValue(entity.getType().getName());
			case ENDER_CRYSTAL:
				return SMiscUtil.getValue(entity.getType().getName());
			case ENDER_DRAGON:
				return SMiscUtil.getValue(entity.getType().getName());
			case ENDERMAN:
				return SMiscUtil.getValue(entity.getType().getName());
			case GHAST:
				return SMiscUtil.getValue(entity.getType().getName());
			case GIANT:
				return SMiscUtil.getValue(entity.getType().getName());
			case IRON_GOLEM:
				return SMiscUtil.getValue(entity.getType().getName());
			case PLAYER:
				return SMiscUtil.getValue(entity.getType().getName());
			case MAGMA_CUBE:
				return SMiscUtil.getValue(entity.getType().getName());
			case MUSHROOM_COW:
				return SMiscUtil.getValue(entity.getType().getName());
			case OCELOT:
				return SMiscUtil.getValue(entity.getType().getName());
			case PIG_ZOMBIE:
				return SMiscUtil.getValue(entity.getType().getName());
			case PIG:
				return SMiscUtil.getValue(entity.getType().getName());
			case SHEEP:
				return SMiscUtil.getValue(entity.getType().getName());
			case SILVERFISH:
				return SMiscUtil.getValue(entity.getType().getName());
			case SKELETON:
				return SMiscUtil.getValue(entity.getType().getName());
			case SLIME:
				return SMiscUtil.getValue(entity.getType().getName());
			case SNOWMAN:
				return SMiscUtil.getValue(entity.getType().getName());
			case SPIDER:
				return SMiscUtil.getValue(entity.getType().getName());
			case SQUID:
				return SMiscUtil.getValue(entity.getType().getName());
			case VILLAGER:
				return SMiscUtil.getValue(entity.getType().getName());
			case WITCH:
				return SMiscUtil.getValue(entity.getType().getName());
			case WITHER_SKULL:
				return SMiscUtil.getValue(entity.getType().getName());
			case WITHER:
				return SMiscUtil.getValue(entity.getType().getName());
			case WOLF:
				return SMiscUtil.getValue(entity.getType().getName());
			case ZOMBIE:
				return SMiscUtil.getValue(entity.getType().getName());

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
				return SMiscUtil.getValue(item.getType().name());
			case ACTIVATOR_RAIL:
				return SMiscUtil.getValue(item.getType().name());
			case ANVIL:
				return SMiscUtil.getValue(item.getType().name());
			case ARROW:
				return SMiscUtil.getValue(item.getType().name());
			case BAKED_POTATO:
				return SMiscUtil.getValue(item.getType().name());
			case BEACON:
				return SMiscUtil.getValue(item.getType().name());
			case BED:
				return SMiscUtil.getValue(item.getType().name());
			case BEDROCK:
				return SMiscUtil.getValue(item.getType().name());
			case BIRCH_WOOD_STAIRS:
				return SMiscUtil.getValue(item.getType().name());
			case BLAZE_POWDER:
				return SMiscUtil.getValue(item.getType().name());
			case BLAZE_ROD:
				return SMiscUtil.getValue(item.getType().name());
			case BOAT:
				return SMiscUtil.getValue(item.getType().name());
			case BONE:
				return SMiscUtil.getValue(item.getType().name());
			case BOOK:
				return SMiscUtil.getValue(item.getType().name());
			case BOOK_AND_QUILL:
				return SMiscUtil.getValue(item.getType().name());
			case BOOKSHELF:
				return SMiscUtil.getValue(item.getType().name());
			case BOW:
				return SMiscUtil.getValue(item.getType().name());
			case BOWL:
				return SMiscUtil.getValue(item.getType().name());
			case BREAD:
				return SMiscUtil.getValue(item.getType().name());
			case BREWING_STAND:
				return SMiscUtil.getValue(item.getType().name());
			case BRICK:
				return SMiscUtil.getValue(item.getType().name());
			case BRICK_STAIRS:
				return SMiscUtil.getValue(item.getType().name());
			case BROWN_MUSHROOM:
				return SMiscUtil.getValue(item.getType().name());
			case BUCKET:
				return SMiscUtil.getValue(item.getType().name());
			case CACTUS:
				return SMiscUtil.getValue(item.getType().name());
			case CAKE:
				return SMiscUtil.getValue(item.getType().name());
			case CARROT:
				return SMiscUtil.getValue(item.getType().name());
			case CARROT_STICK:
				return SMiscUtil.getValue(item.getType().name());
			case CAULDRON:
				return SMiscUtil.getValue(item.getType().name());
			case CHAINMAIL_BOOTS:
				return SMiscUtil.getValue(item.getType().name());
			case CHAINMAIL_CHESTPLATE:
				return SMiscUtil.getValue(item.getType().name());
			case CHAINMAIL_HELMET:
				return SMiscUtil.getValue(item.getType().name());
			case CHAINMAIL_LEGGINGS:
				return SMiscUtil.getValue(item.getType().name());
			case CHEST:
				return SMiscUtil.getValue(item.getType().name());
			case CLAY:
				return SMiscUtil.getValue(item.getType().name());
			case CLAY_BALL:
				return SMiscUtil.getValue(item.getType().name());
			case CLAY_BRICK:
				return SMiscUtil.getValue(item.getType().name());
			case COAL:
				return SMiscUtil.getValue(item.getType().name());
			case COAL_ORE:
				return SMiscUtil.getValue(item.getType().name());
			case COBBLE_WALL:
				return SMiscUtil.getValue(item.getType().name());
			case COBBLESTONE:
				return SMiscUtil.getValue(item.getType().name());
			case COBBLESTONE_STAIRS:
				return SMiscUtil.getValue(item.getType().name());
			case COCOA:
				return SMiscUtil.getValue(item.getType().name());
			case COMMAND:
				return SMiscUtil.getValue(item.getType().name());
			case COMPASS:
				return SMiscUtil.getValue(item.getType().name());
			case COOKIE:
				return SMiscUtil.getValue(item.getType().name());
			case CROPS:
				return SMiscUtil.getValue(item.getType().name());
			case COOKED_BEEF:
				return SMiscUtil.getValue(item.getType().name());
			case COOKED_CHICKEN:
				return SMiscUtil.getValue(item.getType().name());
			case COOKED_FISH:
				return SMiscUtil.getValue(item.getType().name());
			case DAYLIGHT_DETECTOR:
				return SMiscUtil.getValue(item.getType().name());
			case DEAD_BUSH:
				return SMiscUtil.getValue(item.getType().name());
			case DETECTOR_RAIL:
				return SMiscUtil.getValue(item.getType().name());
			case DIAMOND:
				return SMiscUtil.getValue(item.getType().name());
			case DIAMOND_AXE:
				return SMiscUtil.getValue(item.getType().name());
			case DIAMOND_BLOCK:
				return SMiscUtil.getValue(item.getType().name());
			case DIAMOND_BOOTS:
				return SMiscUtil.getValue(item.getType().name());
			case DIAMOND_CHESTPLATE:
				return SMiscUtil.getValue(item.getType().name());
			case DIAMOND_HELMET:
				return SMiscUtil.getValue(item.getType().name());
			case DIAMOND_LEGGINGS:
				return SMiscUtil.getValue(item.getType().name());
			case DIAMOND_HOE:
				return SMiscUtil.getValue(item.getType().name());
			case DIAMOND_SWORD:
				return SMiscUtil.getValue(item.getType().name());
			case DIODE:
				return SMiscUtil.getValue(item.getType().name());
			case DIRT:
				return SMiscUtil.getValue(item.getType().name());
			case DISPENSER:
				return SMiscUtil.getValue(item.getType().name());
			case DOUBLE_STEP:
				return SMiscUtil.getValue(item.getType().name());

				// Unimplemented as of now

			default:
				return 0;
		}
	}
}
