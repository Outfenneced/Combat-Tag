package com.topcat.npclib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import net.minecraft.server.v1_8_R2.Entity;
import net.minecraft.server.v1_8_R2.EntityPlayer;
import net.minecraft.server.v1_8_R2.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R2.PlayerInteractManager;

import com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.topcat.npclib.entity.HumanNPC;
import com.topcat.npclib.entity.NPC;
import com.topcat.npclib.nms.BServer;
import com.topcat.npclib.nms.BWorld;
import com.topcat.npclib.nms.NPCEntity;
import com.topcat.npclib.nms.NPCNetworkManager;

/**
 *
 * @author martin
 */
public class NPCManager implements Listener {

    private HashMap<UUID, NPC> npcs = new HashMap<UUID, NPC>();
    private final BServer server;
    private final Map<World, BWorld> bworlds = new HashMap<World, BWorld>();
    private NPCNetworkManager npcNetworkManager;
    public static JavaPlugin plugin;

    public NPCManager(JavaPlugin plugin) {
        server = BServer.getInstance();

        try {
            npcNetworkManager = new NPCNetworkManager();
        } catch (IOException e) {
        }

        NPCManager.plugin = plugin;
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            @Override
            public void run() {
                HashSet<UUID> toRemove = new HashSet<UUID>();
                for (UUID i : npcs.keySet()) {
                    Entity j = npcs.get(i).getEntity();
                    j.K();
                    if (j.dead) {
                        NPC npc = npcs.get(i);
                        if (npc instanceof HumanNPC) {
                            ((HumanNPC)npc).sendDespawnPacket();
                        }
                        toRemove.add(i);
                    }
                }
                for (UUID n : toRemove) {
                    npcs.remove(n);
                }
            }
        }, 1L, 1L);
        Bukkit.getServer().getPluginManager().registerEvents(new WL(), plugin);
    }

    public BWorld getBWorld(World world) {
        BWorld bworld = bworlds.get(world);
        if (bworld != null) {
            return bworld;
        }
        bworld = new BWorld(world);
        bworlds.put(world, bworld);
        return bworld;
    }

    private class WL implements Listener {

        @EventHandler
        public void onChunkLoad(ChunkLoadEvent event) {
            for (NPC npc : npcs.values()) {
                if (npc != null && event.getChunk() == npc.getBukkitEntity().getLocation().getBlock().getChunk()) {
                    BWorld world = getBWorld(event.getWorld());
                    if (world.getWorldServer().getEntity(npc.getEntity().getUniqueID()) != npc.getEntity()) { //ATTEMPT TO ERRADICATE ENTITY TRACKING ERROR (WORKS IN NORMAL BUKKIT)
                        world.getWorldServer().addEntity(npc.getEntity());
                    }
                }
            }
        }
    }

    public GameProfile setGameProfile(String name) {
        UUID uuid = UUID.randomUUID();
        uuid = new UUID(uuid.getMostSignificantBits() | 0x0000000000005000L, uuid.getLeastSignificantBits());
        while (Bukkit.getServer().getPlayer(uuid) != null) {
            uuid = new UUID(uuid.getMostSignificantBits() | 0x0000000000005000L, uuid.getLeastSignificantBits());
        }
        return new GameProfile(uuid, name);
    }

    public NPC spawnHumanNPC(String name, Location l, UUID id) {
        if (npcs.containsKey(id)) {
            server.getLogger().log(Level.WARNING, "NPC with that id already exists, existing NPC returned");
            return npcs.get(id);
        } else {
            if (name.length() > 16) { // Check and nag if name is too long, spawn NPC anyway with shortened name.
                String tmp = name.substring(0, 16);
                server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
                server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
                name = tmp;
            }
            BWorld world = getBWorld(l.getWorld());
            NPCEntity npcEntity = new NPCEntity(this, world, setGameProfile(name), new PlayerInteractManager(world.getWorldServer()));
            npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
            NPCUtils.sendPacketNearby(l, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npcEntity));
            world.getWorldServer().addEntity(npcEntity); //the right way
            NPC npc = new HumanNPC(npcEntity);
            npcs.put(id, npc);
            return npc;
        }
    }
    
    public void despawnById(UUID playerUUID) {
        NPC npc = npcs.get(playerUUID);
        if (npc != null) {
            npcs.remove(playerUUID);
            if (npc instanceof HumanNPC) {
                ((HumanNPC)npc).sendDespawnPacket();
            }
            npc.removeFromWorld();
        }
    }
    
    public void despawnHumanByName(String npcName) {
        if (npcName.length() > 16) {
            npcName = npcName.substring(0, 16); //Ensure you can still despawn
        }
        for (UUID n : npcs.keySet()) {
            NPC npc = npcs.get(n);
            if (npc instanceof HumanNPC) {
                if (((HumanNPC) npc).getName().equals(npcName)) {
                    despawnById(n);
                }
            }
        }
    }

    public NPC getNPC(UUID playerUUID) {
        return npcs.get(playerUUID);
    }

    public boolean isNPC(org.bukkit.entity.Entity e) {
        return ((CraftEntity) e).getHandle() instanceof NPCEntity;
    }

    public List<NPC> getNPCs() {
        return new ArrayList<NPC>(npcs.values());
    }

    public UUID getNPCIdFromEntity(org.bukkit.entity.Entity e) {
        if (e instanceof HumanEntity) {
            for (UUID i : npcs.keySet()) {
                if (npcs.get(i).getBukkitEntity().getEntityId() == ((HumanEntity) e).getEntityId()) {
                    return i;
                }
            }
        }
        return null;
    }

    public BServer getServer() {
        return server;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
    	EntityPlayer[] npcs = this.npcs.values().toArray(new EntityPlayer[this.npcs.size()]);
    	PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npcs);
    	EntityPlayer player = ((CraftPlayer)event.getPlayer()).getHandle();
    	player.playerConnection.sendPacket(packet);
    	for (NPC npc : this.npcs.values()) {
    	    if (npc instanceof HumanNPC) {
    	        ((HumanNPC)npc).updateEquipment();
    	    }
    	}
    }
    
    public NPCNetworkManager getNPCNetworkManager() {
        return npcNetworkManager;
    }
}
