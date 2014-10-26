package com.trc202.CombatTag;

/*
Temporarily Disable
import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;
import net.slipcor.pvparena.PVPArena;
import net.slipcor.pvparena.api.PVPArenaAPI;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.tommytony.war.Warzone;

public class CombatTagIncompatibles {

    CombatTag plugin;

    public CombatTagIncompatibles(CombatTag combatTag) {
        this.plugin = combatTag;
    }

    public boolean PvPArenaHook(Player plr) {
        Plugin pvparena = this.plugin.getServer().getPluginManager().getPlugin("pvparena");
        boolean notInArena = true;
        if (pvparena != null && (pvparena instanceof PVPArena)) {
            notInArena = "".equals(PVPArenaAPI.getArenaName(plr));
        }
        return notInArena;
    }

    public boolean WarArenaHook(Player plr) {
        boolean notInArena = true;
        if (plugin.getServer().getPluginManager().getPlugin("War") != null) {
            notInArena = Warzone.getZoneByPlayerName(plr.getName()) == null;
        }
        return notInArena;
    }

    public WorldGuardPlugin getWorldGuard() {
        Plugin wg = plugin.getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (wg == null || !(wg instanceof WorldGuardPlugin)) {
            return null;
        }

        return (WorldGuardPlugin) wg;
    }

    public boolean InWGCheck(Player plr) {
        WorldGuardPlugin wg = getWorldGuard();
        if (wg != null) {
            Location plrLoc = plr.getLocation();
            Vector pt = toVector(plrLoc);

            RegionManager regionManager = wg.getRegionManager(plr.getWorld());
            ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
            if (set != null) {
                return set.allows(DefaultFlag.PVP) && !set.allows(DefaultFlag.INVINCIBILITY);
            } else {
                return true;
            }
        }
        return true;
    }

    public boolean notInArena(Player player) {
        return WarArenaHook(player) && PvPArenaHook(player);
    }

    public void startup(PluginManager pm) {
        if (crackShotCheck() != null) {
            pm.registerEvents(new CrackShotListener(), plugin);
        }
    }

    private Plugin crackShotCheck() {
        Plugin cs = plugin.getServer().getPluginManager().getPlugin("CrackShot");

        // CrackShot may not be loaded
        if (cs == null || !(cs instanceof CSDirector)) {
            return null;
        }

        return (Plugin) cs;
    }

    public class CrackShotListener implements Listener {

        public void crackShotEventListener(WeaponDamageEntityEvent e) {
            if (e.isCancelled() || (e.getDamage() == 0)) {
                return;
            }
            Player dmgr = e.getPlayer();

            if (e.getVictim() instanceof Player) {
                Player tagged = (Player) e.getVictim();

                if (plugin.npcm.isNPC(tagged) || plugin.entityListener.disallowedWorld(tagged.getWorld().getName())) {
                    return;
                } //If the damaged player is an npc do nothing

                if ((dmgr instanceof Player) && plugin.settings.playerTag()) {
                    Player damagerPlayer = (Player) dmgr;
                    if (damagerPlayer != tagged) {
                        plugin.entityListener.onPlayerDamageByPlayer(damagerPlayer, tagged);
                    }
                }
            }
        }
    }
}
*/