package techcable.minecraft.combattag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import techcable.minecraft.npclib.NPC;
import techcable.minecraft.npclib.NPCLib;
import techcable.minecraft.npclib.NPCRegistry;

import lombok.*;

@Getter
public class NPCMaster {
	private final JavaPlugin plugin;
	
	public NPCRegistry getRegistry() {
		return NPCLib.getNPCRegistry("CombatTagReloaded");
	}
	
	public NPCMaster(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	public NPC createNPC(UUID player) {
		NPC npc = getRegistry().createNPC(EntityType.PLAYER, player, Bukkit.getOfflinePlayer(player).getName());
		npc.setProtected(false);
		return npc;
	}
	
	public NPC createNPC(Player player) {
		return createNPC(player.getUniqueId());
	}
	
	public NPC getNPC(UUID player) {
		return getRegistry().getByUUID(player);
	}
	
	public NPC getAsNPC(Entity entity) {
		return getRegistry().getAsNPC(entity);
	}
	
	public boolean isNPC(Entity entity) {
		return getRegistry().isNPC(entity);
	}

	public Collection<? extends NPC> getNpcs() {
		return getRegistry().listNpcs();
	}
	
	public UUID getPlayerId(NPC npc) {
		return npc.getUUID();
	}
	
	public void despawn(NPC npc) {
		getRegistry().deregister(npc);
	}
}