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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class SItemUtil
{
	/**
	 * Creates a new item with the given variables.
	 * 
	 * @param material the material that the new item will be.
	 * @param name the name of the new item.
	 * @param lore the lore attached to the new item.
	 * @param enchantments the enchantments attached to the new item.
	 * @return ItemStack
	 */
	public static ItemStack createItem(Material material, String name, List<String> lore, Map<Enchantment, Integer> enchantments)
	{
		// Define variables
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();

		// Set meta data
		if(name != null) meta.setDisplayName(name);
		if(lore != null) meta.setLore(lore);
		item.setItemMeta(meta);

		// Add enchantments if passed in
		if(enchantments != null) item.addUnsafeEnchantments(enchantments);

		return item;
	}

	/**
	 * Creates a book with the given variables.
	 * 
	 * @param title the title of the new book.
	 * @param author the author of the new book.
	 * @param pages the pages of the new book.
	 * @return ItemStack
	 */
	public static ItemStack createBook(String title, String author, List<String> pages)
	{
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();

		meta.setTitle(title);
		meta.setAuthor(author);
		meta.setPages(pages);

		book.setItemMeta(meta);

		return book;
	}

	/**
	 * Creates a book with the given variables.
	 * 
	 * @param title the title of the new book.
	 * @param author the author of the new book.
	 * @param pages the pages of the new book.
	 * @param lore the lore of the new book.
	 * @return ItemStack
	 */
	public static ItemStack createBook(String title, String author, List<String> pages, List<String> lore)
	{
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();

		meta.setTitle(title);
		meta.setAuthor(author);
		meta.setPages(pages);
		if(lore != null) meta.setLore(lore);

		book.setItemMeta(meta);

		return book;
	}

	/**
	 * Creates a chest at <code>location</code> filled with <code>items</code>.
	 * 
	 * @param location the location at which to create the chest.
	 * @param items the ArrayList of items to fill the chest with.
	 */
	public static void createChest(Location location, ArrayList<ItemStack> items)
	{
		// Create the chest
		location.getBlock().setType(Material.CHEST);

		// Get the chest's inventory
		Chest chest = (Chest) location.getBlock().getState();
		Inventory inventory = chest.getBlockInventory();

		// Add the items randomly
		for(ItemStack item : items)
		{
			inventory.setItem((new Random().nextInt(inventory.getSize() - 1) + 1), item);
		}
	}
}
