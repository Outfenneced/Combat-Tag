package com.topcat.npclib;

import net.minecraft.server.v1_8_R1.Packet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NPCUtils {

    private static double getDefaultRadius() {
        return Bukkit.getViewDistance() * 16;
    }

    public static void sendPacketNearby(Location location, Packet packet) {
        sendPacketNearby(location, packet, getDefaultRadius());
    }

    public static void sendPacketNearby(Location location, Packet packet, double radius) {
        radius *= radius;
        final World world = location.getWorld();
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p == null || world != p.getWorld()) {
                continue;
            }
            if (location.distanceSquared(p.getLocation()) > radius) {
                continue;
            }

            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }
    
    public static void sendPacketsNearby(Location location, Packet... packets) {
        sendPacketsNearby(location, packets, getDefaultRadius());
    }
    
    public static void sendPacketsNearby(Location location, Packet[] packets, double radius) {
        radius *= radius;
        final World world = location.getWorld();
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p == null || world != p.getWorld()) {
                continue;
            }
            if (location.distanceSquared(p.getLocation()) > radius) {
                continue;
            }
            
            for (Packet packet : packets) {
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    public static ItemStack[] combineItemStackArrays(Object[] a, Object[] b) {
        ItemStack[] c = new ItemStack[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
