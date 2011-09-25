package org.martin.bukkit.npclib;

import java.util.logging.Level;
import java.util.logging.Logger;
//import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.InventoryLargeChest;
//import net.minecraft.server.PlayerManager;
import net.minecraft.server.TileEntityChest;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author martin
 */
public class BPlayer {

    private CraftPlayer cPlayer;
    //private Player palyer;
    private EntityPlayer ePlayer;
    //private EntityHuman hPlayer;

    public BPlayer(Player player) {
        try {
            //this.palyer = player;
            cPlayer = (CraftPlayer) player;
            ePlayer = cPlayer.getHandle();
            //hPlayer = (EntityHuman) ePlayer;
            //pManager = BServer.getInstance(player.getServer()).getWorld(player.getWorld().getName()).getPlayerManager();
        } catch (Exception ex) {
            Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
        }
    }

    public void openVirtualChest(TileEntityChest chest) {
        ePlayer.a(chest);
    }

    public void openVirtualChest(InventoryLargeChest lChest) {
        ePlayer.a(lChest);
    }
}
