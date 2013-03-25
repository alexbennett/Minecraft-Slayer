/*
	Copyright (c) 2013 The Demigods Team

	Demigods License v1

	This plugin is provided "as is" and without any warranty.  Any express or
	implied warranties, including, but not limited to, the implied warranties
	of merchantability and fitness for a particular purpose are disclaimed.
	In no event shall the authors be liable to any party for any direct,
	indirect, incidental, special, exemplary, or consequential damages arising
	in any way out of the use or misuse of this plugin.

	Definitions

	 1. This Plugin is defined as all of the files within any archive
	    file or any group of files released in conjunction by the Demigods Team,
	    the Demigods Team, or a derived or modified work based on such files.

	 2. A Modification, or a Mod, is defined as this Plugin or a derivative of
	    it with one or more Modification applied to it, or as any program that
	    depends on this Plugin.

	 3. Distribution is defined as allowing one or more other people to in
	    any way download or receive a copy of this Plugin, a Modified
	    Plugin, or a derivative of this Plugin.

	 4. The Software is defined as an installed copy of this Plugin, a
	    Modified Plugin, or a derivative of this Plugin.

	 5. The Demigods Team is defined as Alex Bennett and Alexander Chauncey
	    of http://www.censoredsoftware.com/.

	Agreement

	 1. Permission is hereby granted to use, copy, modify and/or
	    distribute this Plugin, provided that:

	    a. All copyright notices within source files and as generated by
	       the Software as output are retained, unchanged.

	    b. Any Distribution of this Plugin, whether as a Modified Plugin
	       or not, includes this license and is released under the terms
	       of this Agreement. This clause is not dependant upon any
	       measure of changes made to this Plugin.

	    c. This Plugin, Modified Plugins, and derivative works may not
	       be sold or released under any paid license without explicit
	       permission from the Demigods Team. Copying fees for the
	       transport of this Plugin, support fees for installation or
	       other services, and hosting fees for hosting the Software may,
	       however, be imposed.

	    d. Any Distribution of this Plugin, whether as a Modified
	       Plugin or not, requires express written consent from the
	       Demigods Team.

	 2. You may make Modifications to this Plugin or a derivative of it,
	    and distribute your Modifications in a form that is separate from
	    the Plugin. The following restrictions apply to this type of
	    Modification:

	    a. A Modification must not alter or remove any copyright notices
	       in the Software or Plugin, generated or otherwise.

	    b. When a Modification to the Plugin is released, a
	       non-exclusive royalty-free right is granted to the Demigods Team
	       to distribute the Modification in future versions of the
	       Plugin provided such versions remain available under the
	       terms of this Agreement in addition to any other license(s) of
	       the initial developer.

	    c. Any Distribution of a Modified Plugin or derivative requires
	       express written consent from the Demigods Team.

	 3. Permission is hereby also granted to distribute programs which
	    depend on this Plugin, provided that you do not distribute any
	    Modified Plugin without express written consent.

	 4. The Demigods Team reserves the right to change the terms of this
	    Agreement at any time, although those changes are not retroactive
	    to past releases, unless redefining the Demigods Team. Failure to
	    receive notification of a change does not make those changes invalid.
	    A current copy of this Agreement can be found included with the Plugin.

	 5. This Agreement will terminate automatically if you fail to comply
	    with the limitations described herein. Upon termination, you must
	    destroy all copies of this Plugin, the Software, and any
	    derivatives within 48 hours.
 */

package net.alexben.Slayer.Libraries.Objects;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SerialItemStack implements Serializable
{
	private static final long serialVersionUID = -5645654430614861947L;

	private int type;
	private int amount;
	private short durability;
	private HashMap<Integer, Integer> enchantments = new HashMap<Integer, Integer>();
	private String displayName = null, author = null, title = null;
	private List<String> lore = null, pages = null;
	private Map<String, Object> bookMeta = null;

	public SerialItemStack(ItemStack item)
	{
		this.type = item.getTypeId();
		this.durability = item.getDurability();
		this.amount = item.getAmount();

		if(item.hasItemMeta())
		{
			if(item.getType().equals(Material.WRITTEN_BOOK))
			{
				BookMeta bookMeta = (BookMeta) item.getItemMeta();

				this.bookMeta = bookMeta.serialize();

				if(bookMeta.hasAuthor()) this.author = bookMeta.getAuthor();
				if(bookMeta.hasPages()) this.pages = bookMeta.getPages();
				if(bookMeta.hasLore()) this.lore = bookMeta.getLore();
				if(bookMeta.hasTitle()) this.title = bookMeta.getTitle();
				if(bookMeta.hasDisplayName()) this.displayName = bookMeta.getDisplayName();

				if(bookMeta.hasEnchants())
				{
					for(Entry<Enchantment, Integer> ench : bookMeta.getEnchants().entrySet())
					{
						this.enchantments.put(ench.getKey().getId(), ench.getValue());
					}
				}
			}

			if(item.getItemMeta().hasEnchants())
			{
				for(Entry<Enchantment, Integer> ench : item.getEnchantments().entrySet())
				{
					this.enchantments.put(ench.getKey().getId(), ench.getValue());
				}
			}
			if(item.getItemMeta().hasDisplayName()) this.displayName = item.getItemMeta().getDisplayName();
			if(item.getItemMeta().hasLore()) this.lore = item.getItemMeta().getLore();
		}
	}

    /**
     * Returns a usable <code>ItemStack</code>.
     *
     * @return ItemStack
     */
	public ItemStack toItemStack()
	{
		ItemStack item = new ItemStack(this.type, this.amount);

		if(item.getType().equals(Material.WRITTEN_BOOK))
		{
			BookMeta meta = (BookMeta) item.getItemMeta();
			if(this.title != null) meta.setTitle(this.title);
			if(this.author != null) meta.setAuthor(this.author);
			if(this.pages != null) meta.setPages(this.pages);
			if(this.lore != null) meta.setLore(this.lore);
			if(this.displayName != null) meta.setDisplayName(this.displayName);
			item.setItemMeta(meta);
		}
		else
		{
			ItemMeta meta = item.getItemMeta();
			if(this.displayName != null) meta.setDisplayName(this.displayName);
			if(this.lore != null) meta.setLore(this.lore);
			item.setItemMeta(meta);
		}

		if(this.enchantments != null)
		{
			for(Entry<Integer, Integer> ench : this.enchantments.entrySet())
			{
				item.addEnchantment(Enchantment.getById(ench.getKey()), ench.getValue());
			}
		}

		// Set data for the Item
		item.setAmount(this.amount);
		item.setDurability(this.durability);

		return item;
	}
}
