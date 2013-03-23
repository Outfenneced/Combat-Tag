package com.topcat.npclib.nms;

import java.io.IOException;
import java.lang.reflect.Field;

import net.minecraft.server.v1_5_R2.Connection;
import net.minecraft.server.v1_5_R2.ConsoleLogManager;
import net.minecraft.server.v1_5_R2.NetworkManager;
import net.minecraft.server.v1_5_R2.Packet;

/**
 *
 * @author martin
 */
public class NPCNetworkManager extends NetworkManager {

	NPCEntity npc;
	
	public NPCNetworkManager() throws IOException {
		//ConsoleLogManager, when declared in this way, creates 2 new files every load of plugin
		super(new ConsoleLogManager("NPC Manager", (String) null, (String) null), new NullSocket(), "NPC Manager", new Connection() {
			@Override
			public boolean a() {
				return true;
			}
		}, null);
		try {
			Field f = NetworkManager.class.getDeclaredField("n");
			f.setAccessible(true);
			f.set(this, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void a(Connection nethandler) {
	}

	@Override
	public void queue(Packet packet) {
	}

	@Override
	public void a(String s, Object... aobject) {
	}

	@Override
	public void a() {
	}

}