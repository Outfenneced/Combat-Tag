package com.trc202.CombatTagListeners;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.NetworkManager;

import com.trc202.CombatTag.CombatTag;

public class CombatTagPacketListener extends NetServerHandler {
	public CombatTagPacketListener(MinecraftServer minecraftserver,
			NetworkManager networkmanager, EntityPlayer entityplayer) {
		super(minecraftserver, networkmanager, entityplayer);
		// TODO Auto-generated constructor stub
	}

	CombatTag plugin;

}
