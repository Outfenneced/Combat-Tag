package com.topcat.npclib.nms;

import java.io.IOException;
import java.lang.reflect.Field;

import net.minecraft.server.v1_8_R1.NetworkManager;
import net.minecraft.util.io.netty.channel.Channel;

/**
 *
 * @author martin
 */
public class NPCNetworkManager extends NetworkManager {

    public NPCNetworkManager() throws IOException {
        super(EnumProtocolDirection.CLIENTBOUND);
        Field channel = ReflectUtil.makeField(NetworkManager.class, "i"); 
        Field address = ReflectUtil.makeField(NetworkManager.class, "j");
        
        ReflectUtil.setField(channel, this, new NullChannel());
        ReflectUtil.setField(address, this, new NullSocketAddress());
    }

    public static Field getField(Class<?> clazz, String field) {
        if (clazz == null) {
            return null;
        }
        Field f = null;
        try {
            f = clazz.getDeclaredField(field);
            f.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }

    public static void stopNetworkThreads(NetworkManager manager) {
        if (THREAD_STOPPER == null) {
            return;
        }
        try {
            Channel channel = (Channel) THREAD_STOPPER.get(manager);
            channel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final Field THREAD_STOPPER = getField(NetworkManager.class, "m");
}
