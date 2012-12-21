package com.topcat.npclib.nms;

import net.minecraft.server.v1_4_5.EntityPlayer;
import net.minecraft.server.v1_4_5.NetServerHandler;
import net.minecraft.server.v1_4_5.Packet;
import net.minecraft.server.v1_4_5.Packet101CloseWindow;
import net.minecraft.server.v1_4_5.Packet102WindowClick;
import net.minecraft.server.v1_4_5.Packet106Transaction;
import net.minecraft.server.v1_4_5.Packet10Flying;
import net.minecraft.server.v1_4_5.Packet130UpdateSign;
import net.minecraft.server.v1_4_5.Packet14BlockDig;
import net.minecraft.server.v1_4_5.Packet15Place;
import net.minecraft.server.v1_4_5.Packet16BlockItemSwitch;
import net.minecraft.server.v1_4_5.Packet18ArmAnimation;
import net.minecraft.server.v1_4_5.Packet19EntityAction;
import net.minecraft.server.v1_4_5.Packet255KickDisconnect;
import net.minecraft.server.v1_4_5.Packet3Chat;
import net.minecraft.server.v1_4_5.Packet7UseEntity;
import net.minecraft.server.v1_4_5.Packet9Respawn;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_4_5.CraftServer;
import org.bukkit.craftbukkit.v1_4_5.entity.CraftPlayer;

import com.topcat.npclib.NPCManager;

/**
 * 
 * @author martin
 */
public class NPCNetHandler extends NetServerHandler {

	public NPCNetHandler(NPCManager npcManager, EntityPlayer entityplayer) {
		super(npcManager.getServer().getMCServer(), npcManager.getNPCNetworkManager(), entityplayer);
	}

	@Override
	public CraftPlayer getPlayer() {
		return new CraftPlayer((CraftServer) Bukkit.getServer(), player); //Fake player prevents spout NPEs
	}

	@Override
	public void d() {
	};

	@Override
	public void a(Packet10Flying packet10flying) {
	};

	@Override
	public void a(double d0, double d1, double d2, float f, float f1) {
	};

	@Override
	public void a(Packet14BlockDig packet14blockdig) {
	};

	@Override
	public void a(Packet15Place packet15place) {
	};

	@Override
	public void a(String s, Object[] aobject) {
	};

	@Override
	public void onUnhandledPacket(Packet packet) {
	};

	@Override
	public void a(Packet16BlockItemSwitch packet16blockitemswitch) {
	};

	@Override
	public void a(Packet3Chat packet3chat) {
	};

	@Override
	public void a(Packet18ArmAnimation packet18armanimation) {
	};

	@Override
	public void a(Packet19EntityAction packet19entityaction) {
	};

	@Override
	public void a(Packet255KickDisconnect packet255kickdisconnect) {
	};

	@Override
	public void sendPacket(Packet packet) {
	};

	@Override
	public void a(Packet7UseEntity packet7useentity) {
	};

	@Override
	public void a(Packet9Respawn packet9respawn) {
	};

	@Override
	public void handleContainerClose(Packet101CloseWindow packet101closewindow) {
	};

	@Override
	public void a(Packet102WindowClick packet102windowclick) {
	};

	@Override
	public void a(Packet106Transaction packet106transaction) {
	};

	@Override
	public int lowPriorityCount() {
		return super.lowPriorityCount();
	}

	@Override
	public void a(Packet130UpdateSign packet130updatesign) {
	};

}