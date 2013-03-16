package com.topcat.npclib.nms;

import java.io.IOException;
import java.lang.reflect.Field;

import net.minecraft.server.v1_5_R1.Connection;
import net.minecraft.server.v1_5_R1.ConsoleLogManager;
import net.minecraft.server.v1_5_R1.NetworkManager;
import net.minecraft.server.v1_5_R1.Packet;

/**
 *
 * @author martin
 */
public class NPCNetworkManager extends NetworkManager {

	public NPCNetworkManager() throws IOException {
		//Unsure of the ConsoleLogManager. May cause trouble.
		super(new ConsoleLogManager("NPC Manager", (String) null, (String) null),new NullSocket(), "NPC Manager", new Connection() {
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