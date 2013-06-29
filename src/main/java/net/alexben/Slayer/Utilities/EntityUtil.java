package net.alexben.Slayer.Utilities;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Handles entity-specific methods.
 */
public class EntityUtil
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
		long time = System.currentTimeMillis() + (ConfigUtil.getSettingInt("data.entity_tracking_period") * 1000);

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
			if(ObjUtil.toLong(entity.getValue().get("time")) <= System.currentTimeMillis())
			{
				entities.remove(entity.getKey());
			}
		}
	}
}
