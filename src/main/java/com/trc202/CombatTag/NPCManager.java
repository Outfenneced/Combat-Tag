package com.trc202.CombatTag;

import net.techcable.npclib.NPC;
import net.techcable.npclib.NPCLib;
import net.techcable.npclib.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Collection;
import java.util.UUID;

/**
 * Manages the npcs
 */
public class NPCManager {
    public NPCManager(CombatTag plugin) {
        this.plugin = plugin;
    }

    private final CombatTag plugin;

    public Collection<? extends NPC> getNPCs() {
        return getRegistry().listNpcs();
    }

    public UUID getNPCIdFromEntity(Entity e) {
        NPC npc = getRegistry().getAsNPC(e);
        if (npc == null) return null;
        return npc.getUUID();
    }

    public NPCRegistry getRegistry() {
        return NPCLib.getNPCRegistry("CombatTag", plugin);
    }

    public NPC spawnHumanNPC(String npcName, Location location, UUID uniqueId) {
        NPC npc = getRegistry().createNPC(EntityType.PLAYER, uniqueId, npcName);
        npc.setSkin(uniqueId); //Skins!!!
        npc.spawn(location);
        return npc;
    }

    public NPC getNPC(UUID playerUUID) {
        return getRegistry().getByUUID(playerUUID);
    }

    public void despawnById(UUID playerUUID) {
        NPC npc = getNPC(playerUUID);
        npc.despawn();
    }

    public boolean isNPC(Entity entity) {
        return getRegistry().isNPC(entity);
    }
}
