package techcable.minecraft.combattag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import lombok.*;

@Getter
public class NPCMaster {
	private final JavaPlugin plugin;
	
	private NPCRegistry registry;
	
	private BiMap<UUID, UUID> playerToNpc = HashBiMap.create();
	private int lastId;
	public NPCMaster(JavaPlugin plugin) {
		this.plugin = plugin;
		registry = CitizensAPI.getNPCRegistry();
	}
	
	public NPC createNPC(UUID player) {
		NPC npc = registry.createNPC(EntityType.PLAYER, Bukkit.getOfflinePlayer(player).getName());
		playerToNpc.put(player, npc.getUniqueId());
		return npc;
	}
	
	public NPC createNPC(Player player) {
		return createNPC(player.getUniqueId());
	}
	
	public NPC getNPC(UUID player) {
		return registry.getByUniqueId(playerToNpc.get(player));
	}
	
	public NPC getAsNPC(Entity entity) {
		return registry.getNPC(entity);
	}
	
	public boolean isNPC(Entity entity) {
		return registry.isNPC(entity) && playerToNpc.containsKey(getAsNPC(entity).getUniqueId());
	}

	public List<NPC> getNpcs() {
		List<NPC> npcs = new ArrayList<>();
		for (UUID realId : playerToNpc.values()) {
			npcs.add(registry.getByUniqueId(realId));
		}
		return npcs;
	}
	
	public UUID getPlayerId(NPC npc) {
		return playerToNpc.inverse().get(npc.getUniqueId());
	}
	
	public void despawn(NPC npc) {
		playerToNpc.inverse().remove(npc.getUniqueId());
		npc.despawn();
	}
}