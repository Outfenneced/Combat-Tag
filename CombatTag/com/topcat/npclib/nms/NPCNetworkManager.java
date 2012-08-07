package com.topcat.npclib.nms;

import net.minecraft.server.NetHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;

import java.lang.reflect.Field;

/**
 *
 * @author martin
 */
public class NPCNetworkManager extends NetworkManager {

	public NPCNetworkManager() {
		super(new NullSocket(), "NPC Manager", new NetHandler() {
            @Override
            public boolean a() {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }
        },null);
		try {
			Field f = NetworkManager.class.getDeclaredField("m");
			f.setAccessible(true);
			f.set(this, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void a(NetHandler nethandler) {
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