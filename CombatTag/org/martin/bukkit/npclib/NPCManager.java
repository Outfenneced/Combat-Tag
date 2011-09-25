package org.martin.bukkit.npclib;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import net.minecraft.server.Entity;

import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author martin
 */
public class NPCManager {

    private HashMap<String, NPCEntity> npcs = new HashMap<String, NPCEntity>();
    private BServer server;
    private int taskid;
    private JavaPlugin plugin;

    public NPCManager(JavaPlugin plugin) {
        server = BServer.getInstance(plugin);
        this.plugin = plugin;
        taskid = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                HashSet<String> toRemove = new HashSet<String>();
                for (String i : npcs.keySet()) {
                    Entity j = npcs.get(i);
                    j.aa();//j.R(); was renamed
                    if (j.dead) {
                        toRemove.add(i);
                    }
                }
                for (String n : toRemove) {
                    npcs.remove(n);
                }
            }
        }, 1L, 1L);
        plugin.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_DISABLE, new SL(), Priority.Normal, plugin);
        plugin.getServer().getPluginManager().registerEvent(Event.Type.CHUNK_LOAD, new WL(), Priority.Normal, plugin);
    }
    
    private class SL extends ServerListener {
        @Override
        public void onPluginDisable(PluginDisableEvent event) {
            if (event.getPlugin() == plugin) {
                despawnAll();
                plugin.getServer().getScheduler().cancelTask(taskid);
            }
        }
    }
    
    private class WL extends WorldListener {
        @Override
        public void onChunkLoad(ChunkLoadEvent event) {
            for (NPCEntity npc : npcs.values()) {
                if (npc != null && event.getChunk() == npc.getBukkitEntity().getLocation().getBlock().getChunk()) {
                    BWorld world = new BWorld(event.getWorld());
                    world.getWorldServer().addEntity(npc);
                }
            }
        }
    }

    public NPCEntity spawnNPC(String name, Location l) {
        int i = 0;
        String id = name;
        while (npcs.containsKey(id)) {
            id = name + i;
            i++;
        }
        return spawnNPC(name, l, id);
    }

    public NPCEntity spawnNPC(String name, Location l, String id) {
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
            BWorld world = new BWorld(l.getWorld());
            NPCEntity npcEntity = new NPCEntity(server.getMCServer(), world.getWorldServer(), name, new ItemInWorldManager(world.getWorldServer()));
            npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
            world.getWorldServer().addEntity(npcEntity); //the right way
            npcs.put(id, npcEntity);
            return npcEntity;
        }
    }

    public void despawnById(String id) {
        NPCEntity npc = npcs.get(id);
        if (npc != null) {
            npcs.remove(id);
            try {
                npc.world.removeEntity(npc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void despawn(String npcName) {
        if (npcName.length() > 16) {
            npcName = npcName.substring(0, 16); //Ensure you can still despawn
        }
        HashSet<String> toRemove = new HashSet<String>();
        for (String n : npcs.keySet()) {
            NPCEntity npc = npcs.get(n);
            if (npc != null && npc.name.equals(npcName)) {
                toRemove.add(n);
                try {
                    npc.world.removeEntity(npc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        for (String n : toRemove) {
            npcs.remove(n);
        }
    }
    
    public void despawnAll() {
        for (NPCEntity npc : npcs.values()) {
            if (npc != null) {
                try {
                    npc.world.removeEntity(npc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        npcs.clear();
    }
    
    public void pathFindNPC(String id, Location l) {
        pathFindNPC(id, l, 1500);
    }
    
    public void pathFindNPC(String id, Location l, int maxIterations) {
        NPCEntity npc = npcs.get(id);
        if (npc != null) {
            if (l.getWorld() == npc.getBukkitEntity().getWorld()) {
                npc.pathFindTo(l, maxIterations);
            } else {
                String n = npc.name;
                despawnById(id);
                spawnNPC(n, l, id);
            }
        }
    }

    public void moveNPC(String id, Location l) {
        NPCEntity npc = npcs.get(id);
        if (npc != null) {
            npc.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
        }
    }

    public void moveNPCStatic(String id, Location l) {
        NPCEntity npc = npcs.get(id);
        if (npc != null) {
            npc.setPosition(l.getX(), l.getY(), l.getZ());
        }
    }

    public void putNPCinbed(String id, Location bed) {
        NPCEntity npc = npcs.get(id);
        if (npc != null) {
            npc.setPosition(bed.getX(), bed.getY(), bed.getZ());
            npc.a((int) bed.getX(), (int) bed.getY(), (int) bed.getZ());
        }
    }
    
    public void getNPCoutofbed(String id) {
        NPCEntity npc = npcs.get(id);
        if (npc != null) {
            npc.a(true, true, true);
        }
    }

    public void setSneaking(String id, boolean flag) {
        NPCEntity npc = npcs.get(id);
        if (npc != null) {
            npc.setSneak(flag);
        }
    }

    public NPCEntity getNPC(String id) {
        return npcs.get(id);
    }

    public boolean isNPC(org.bukkit.entity.Entity e) {
        return (((CraftEntity) e).getHandle() instanceof NPCEntity);
    }

    public List<NPCEntity> getNPCsByName(String name) {
        List<NPCEntity> ret = new ArrayList<NPCEntity>();
        Collection<NPCEntity> i = npcs.values();
        for (NPCEntity e : i) {
            if (e.getName().equalsIgnoreCase(name)) {
                ret.add(e);
            }
        }
        return ret;
    }

    public List<NPCEntity> getNPCs(){
        return new ArrayList<NPCEntity>(npcs.values());
    }

    public String getNPCIdFromEntity(org.bukkit.entity.Entity e) {
        if (e instanceof HumanEntity) {
            for (String i : npcs.keySet()) {
                if (npcs.get(i).getBukkitEntity().getEntityId() == ((HumanEntity) e).getEntityId()) {
                    return i;
                }
            }
        }
        return null;
    }
    
    public void rename(String id, String name) {
        if (name.length() > 16) { // Check and nag if name is too long, spawn NPC anyway with shortened name.
            String tmp = name.substring(0, 16);
            server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
            server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
            name = tmp;
        }
        NPCEntity npc = getNPC(id);
        npc.setName(name);
        BWorld b = new BWorld(npc.getBukkitEntity().getLocation().getWorld());
        WorldServer s = b.getWorldServer();
        try {
            Method m = s.getClass().getDeclaredMethod("d", new Class[]{Entity.class});
            m.setAccessible(true);
            m.invoke(s, (Entity) npc);
            m = s.getClass().getDeclaredMethod("c", new Class[]{Entity.class});
            m.setAccessible(true);
            m.invoke(s, (Entity) npc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        s.everyoneSleeping();
    }
}
