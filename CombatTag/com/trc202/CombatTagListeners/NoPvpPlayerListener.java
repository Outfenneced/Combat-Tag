package com.trc202.CombatTagListeners;

import net.minecraft.server.v1_4_R1.EntityPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.topcat.npclib.NPCManager;
import com.topcat.npclib.entity.NPC;
import com.trc202.CombatTag.CombatTag;
import com.trc202.Containers.PlayerDataContainer;

public class NoPvpPlayerListener implements Listener{
	
	private final CombatTag plugin;
	public static int explosionDamage = -1;
	public NPCManager npcm;
	public NoPvpEntityListener entityListener;
	
    public NoPvpPlayerListener(CombatTag instance) {
    	plugin = instance;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event){
		Player loginPlayer = event.getPlayer();
		if(plugin.hasDataContainer(loginPlayer.getName())){
			//Player has a data container and is likely to need some sort of punishment
			PlayerDataContainer loginDataContainer = plugin.getPlayerData(loginPlayer.getName());
			if(loginDataContainer.hasSpawnedNPC()){
				//Player has pvplogged and has not been killed yet
				//despawn the npc and transfer any effects over to the player
				CraftPlayer cPlayer = (CraftPlayer) loginPlayer;
				EntityPlayer ePlayer = cPlayer.getHandle();
				ePlayer.invulnerableTicks = 0;
				plugin.despawnNPC(loginDataContainer);
			}
			if(loginDataContainer.shouldBePunished()){
				loginPlayer.setExp(loginDataContainer.getExp());
				loginPlayer.getInventory().setArmorContents(loginDataContainer.getPlayerArmor());
				loginPlayer.getInventory().setContents(loginDataContainer.getPlayerInventory());
				int healthSet = plugin.healthCheck(loginDataContainer.getHealth());
				loginPlayer.setHealth(healthSet);
				assert(loginPlayer.getHealth() == loginDataContainer.getHealth());
				loginPlayer.setLastDamageCause(new EntityDamageEvent(loginPlayer, DamageCause.ENTITY_EXPLOSION, 0));
				loginPlayer.setNoDamageTicks(0);
			}
			if(loginPlayer.getHealth() > 0){
				loginDataContainer.setPvPTimeout(plugin.getTagDuration());
			}
			loginDataContainer.setShouldBePunished(false);
			loginDataContainer.setSpawnedNPC(false);
		}
	}
	
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent e){
		Player quitPlr = e.getPlayer();
		if(quitPlr.isDead()){
			plugin.entityListener.onPlayerDeath(quitPlr);
		}
		else if(plugin.hasDataContainer(quitPlr.getName())){
			//Player is likely in pvp
			PlayerDataContainer quitDataContainer = plugin.getPlayerData(quitPlr.getName());
			if(!quitDataContainer.hasPVPtagExpired()){
				//Player has logged out before the pvp battle is considered over by the plugin
				if(plugin.isDebugEnabled()){plugin.log.info("[CombatTag] " + quitPlr.getName() + " has logged of during pvp!");}
				if(plugin.settings.isInstaKill() || quitPlr.getHealth() <= 0){
					plugin.log.info("[CombatTag] " + quitPlr.getName() + " has been instakilled!");
					quitPlr.setHealth(0);
					plugin.removeDataContainer(quitPlr.getName());
				}else{
					boolean willSpawn = true;
					if(plugin.settings.dontSpawnInWG()){
						willSpawn = plugin.ctIncompatible.InWGCheck(quitPlr);
					}
					if(willSpawn){
						NPC npc = plugin.spawnNpc(quitPlr, quitPlr.getLocation());
						if(npc.getBukkitEntity() instanceof Player){
							Player npcPlayer = (Player) npc.getBukkitEntity();
							plugin.copyContentsNpc(npc, quitPlr);
							plugin.npcm.rename(quitPlr.getName(), plugin.getNpcName(quitPlr.getName()));
							int healthSet = plugin.healthCheck(quitPlr.getHealth());
							npcPlayer.setHealth(healthSet);
							quitDataContainer.setSpawnedNPC(true);
							quitDataContainer.setNPCId(quitPlr.getName());
							quitDataContainer.setShouldBePunished(false);
							quitPlr.getWorld().createExplosion(quitPlr.getLocation(), explosionDamage); //Create the smoke effect //
							if(plugin.settings.getNpcDespawnTime() > 0){
								plugin.scheduleDelayedKill(npc, quitDataContainer);
							}
						}
					}
				}
			}
		}
	}
	
    @EventHandler(ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event){
    	Player player = event.getPlayer();
    	if (plugin.hasDataContainer(player.getName())) {
    		PlayerDataContainer kickDataContainer = plugin.getPlayerData(player.getName());
    		if (!kickDataContainer.hasPVPtagExpired()) {
    			if (plugin.settings.dropTagOnKick()) {
    				if (plugin.isDebugEnabled()) {plugin.log.info("[CombatTag] Player tag dropped for being kicked.");}
    				kickDataContainer.setPvPTimeout(0);
    				plugin.removeDataContainer(player.getName());
    			}
    		}
    	}
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event){
    	if(plugin.hasDataContainer(event.getPlayer().getName())){
    		PlayerDataContainer playerData = plugin.getPlayerData(event.getPlayer().getName());
    		if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getItem().getType() == Material.ENDER_PEARL && !playerData.hasPVPtagExpired() && plugin.settings.blockEnderPearl()){
    			event.getPlayer().sendMessage(ChatColor.RED + "[CombatTag] You can't ender pearl while tagged.");
    			event.setCancelled(true);
    		}
    	}
    }
    
	@EventHandler(priority = EventPriority.LOWEST)
	public void onTeleport(PlayerTeleportEvent event){
		if(plugin.hasDataContainer(event.getPlayer().getName())){
			PlayerDataContainer playerData = plugin.getPlayerData(event.getPlayer().getName());
			if(plugin.settings.blockTeleport() == true && !playerData.hasPVPtagExpired()){
				TeleportCause cause = event.getCause();
				if(cause == TeleportCause.PLUGIN || cause == TeleportCause.COMMAND){
					event.getPlayer().sendMessage(ChatColor.RED + "[CombatTag] You can't teleport while tagged.");
					event.setCancelled(true);
				}
			}
		}
	}
}
