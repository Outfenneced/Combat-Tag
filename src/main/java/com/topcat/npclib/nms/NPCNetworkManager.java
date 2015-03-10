package com.topcat.npclib.nms;

import com.google.common.base.Throwables;
import java.io.IOException;
import java.lang.reflect.Field;

import net.minecraft.server.v1_8_R2.EnumProtocolDirection;
import net.minecraft.server.v1_8_R2.NetworkManager;
import io.netty.channel.Channel;

/**
 *
 * @author martin
 */
public class NPCNetworkManager extends NetworkManager {

    public NPCNetworkManager() throws IOException {
        super(EnumProtocolDirection.CLIENTBOUND);
        Field channel = getField(NetworkManager.class, "k"); 
        Field address = getField(NetworkManager.class, "l");
        
        setField(channel, this, new NullChannel());
        setField(address, this, new NullSocketAddress());
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
            throw Throwables.propagate(e);
        }
        return f;
    }
	public static void setField(Field field, Object objToSet, Object value) {
		field.setAccessible(true);
		try {
			field.set(objToSet, value);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
		    throw new RuntimeException(e);
		}
	}
}
