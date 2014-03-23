package com.topcat.npclib.nms;

import net.minecraft.server.v1_7_R2.EntityPlayer;
import net.minecraft.server.v1_7_R2.Packet;
import net.minecraft.server.v1_7_R2.PlayerConnection;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R2.CraftServer;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;

import com.topcat.npclib.NPCManager;

/**
 *
 * @author martin
 */
public class NPCPlayerConnection extends PlayerConnection {

	public NPCPlayerConnection(NPCManager npcManager, EntityPlayer entityplayer) {
		super(npcManager.getServer().getMCServer(), npcManager.getNPCNetworkManager(), entityplayer);
	}

	@Override
	public CraftPlayer getPlayer() {
		return new CraftPlayer((CraftServer) Bukkit.getServer(), player); //Fake player prevents spout NPEs
	};

	@Override
	public void sendPacket(Packet packet) {
	};

}