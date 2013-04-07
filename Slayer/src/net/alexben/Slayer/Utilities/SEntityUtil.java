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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Handles entity-specific methods.
 */
public class SEntityUtil
{
	// Define entity unique id Map
	private static Map<Integer, Map<String, Object>> entities = new HashMap<Integer, Map<String, Object>>();

	/**
	 * Adds the <code>entity</code> to the entities Map.
	 * 
	 * @param entity the entity to add.
	 */
	public static void addEntity(Entity entity, CreatureSpawnEvent.SpawnReason reason)
	{
		long time = System.currentTimeMillis() + (SConfigUtil.getSettingInt("data.entity_tracking_period") * 1000);

		entities.put(entity.getEntityId(), new HashMap<String, Object>());
		entities.get(entity.getEntityId()).put("time", time);
		entities.get(entity.getEntityId()).put("reason", reason);
	}

	/**
	 * Removes the <code>entity</code> from the entities Map.
	 * 
	 * @param entity the entity to remove.
	 */
	public static void removeEntity(Entity entity)
	{
		if(entities.containsKey(entity.getEntityId()))
		{
			entities.remove(entity.getEntityId());
		}
	}

	/**
	 * Returns true if the entity was spawned by an entity.
	 * 
	 * @param entity the entity to check.
	 */
	public static boolean isSpawnerEntity(Entity entity)
	{
		if(entities.containsKey(entity.getEntityId()))
		{
			if(entities.get(entity.getEntityId()).get("reason").equals(CreatureSpawnEvent.SpawnReason.SPAWNER))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the full entity Map.
	 * 
	 * @return Map
	 */
	public static Map<Integer, Map<String, Object>> getEntityMap()
	{
		return entities;
	}

	/**
	 * Clears old entries out of the entity map.
	 */
	public static void cleanupMap()
	{
		if(entities == null || entities.isEmpty()) return;

		for(Map.Entry<Integer, Map<String, Object>> entity : entities.entrySet())
		{
			if(SObjUtil.toLong(entity.getValue().get("time")) <= System.currentTimeMillis())
			{
				entities.remove(entity.getKey());
			}
		}
	}
}
