/**
* The MIT License
* Copyright (c) 2015 Techcable
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/

package com.trc202.CombatTag;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
 
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
 
/**
* Loads the data of offline players
*
* Should be compatible with versions later than 1.6.4
*
* @author Techcable
*/
public class OfflinePlayerLoader {
/**
* Returns the given players data
*
* Loads the player's data from its file if it is offline
* If the player is online the online version is returned
*
* Players returned by this method may or may not be spawned and should only be used to access data
*
* @param name the player's name
*
* @return a player's data
*
* @throws RuntimeException if the loading failed
*/
public static Player loadPlayer(String name) {
return loadPlayer(Bukkit.getOfflinePlayer(name));
}
/**
* Returns the given players data
*
* Loads the player's data from its file if it is offline
* If the player is online the online version is returned
*
* Players returned by this method may or may not be spawned and should only be used to access data
*
* @param id the player's uuid
*
* @return a player's data
*
* @throws RuntimeException if the loading failed
*/
public static Player loadPlayer(UUID id) {
return loadPlayer(Bukkit.getOfflinePlayer(id));
}
/**
* Returns the given players data
*
* Loads the player's data from its file if it is offline
* If the player is online the online version is returned
*
* Players returned by this method may or may not be spawned and should only be used to access data
*
* @param player the player
*
* @return a player's data
*
* @throws RuntimeException if the loading failed
*/
public static Player loadPlayer(OfflinePlayer player) {
if (player == null) return null;
if (player instanceof Player) {
return (Player) player;
}
return loadPlayer(player.getUniqueId(), player.getName());
}
private static Player loadPlayer(UUID id, String name) {
Object server = getHandle(Bukkit.getServer());
Object interactManager = newPlayerInteractManager();
Object worldServer = getWorldServer();
Object profile = newGameProfile(id, name);
Class<?> entityPlayerClass = getNmsClass("EntityPlayer");
Constructor entityPlayerConstructor = makeConstructor(entityPlayerClass, server.getClass(), worldServer.getClass(), profile.getClass(), interactManager.getClass());
Object entityPlayer = callConstructor(entityPlayerConstructor, server, worldServer, profile, interactManager);
Player player = (Player) getBukkitEntity(entityPlayer);
return player;
}
private static Object newGameProfile(UUID id, String name) {
Class<?> gameProfileClass = getUtilClass("com.mojang.authlib.GameProfile");
if (gameProfileClass == null) { //Before uuids
return name;
}
Constructor gameProfileConstructor = null;
gameProfileConstructor = makeConstructor(gameProfileClass, UUID.class, String.class);
if (gameProfileConstructor == null) { //Verson has string constructor
gameProfileConstructor = makeConstructor(gameProfileClass, String.class, String.class);
return callConstructor(gameProfileConstructor, id.toString(), name);
} else { //Version has uuid constructor
return callConstructor(gameProfileConstructor, id, name);
}
}
private static Object newPlayerInteractManager() {
Object worldServer = getWorldServer();
Class<?> playerInteractClass = getNmsClass("PlayerInteractManager");
Constructor c = makeConstructor(playerInteractClass, worldServer.getClass());
return callConstructor(c, worldServer);
}
private static Object getWorldServer() {
Object server = getHandle(Bukkit.getServer());
Method getWorldServer = makeMethod(server.getClass(), "getWorldServer", int.class);
return callMethod(getWorldServer, server, 0);
}
//NMS Utils
private static Entity getBukkitEntity(Object o) {
Method getBukkitEntity = makeMethod(o.getClass(), "getBukkitEntity");
return callMethod(getBukkitEntity, o);
}
private static Class<?> getNmsClass(String name) {
String className = "net.minecraft.server" + getVersion() + "." + name;
try {
return Class.forName(className);
} catch (ClassNotFoundException ex) {
return null;
}
}
private static Class<?> getUtilClass(String name) {
try {
return Class.forName(name); //Try before 1.8 first
} catch (ClassNotFoundException ex) {
try {
return Class.forName("net.minecraft.util." + name); //Not 1.8
} catch (ClassNotFoundException ex2) {
return null;
}
}
}
private static String getVersion() {
Bukkit.getServer();
String packageName = Bukkit.getServer().getClass().getPackage().getName();
return packageName.substring(packageName.lastIndexOf('.') + 1);
}
private static Object getHandle(Object wrapper) {
Method getHandle = makeMethod(wrapper.getClass(), "getHandle");
return callMethod(getHandle, wrapper);
}
//Utils
private static Method makeMethod(Class<?> clazz, String methodName, Class<?>... paramaters) {
try {
return clazz.getDeclaredMethod(methodName, paramaters);
} catch (NoSuchMethodException ex) {
return null;
} catch (Exception ex) {
throw new RuntimeException(ex);
}
}
private static <T> T callMethod(Method method, Object instance, Object... paramaters) {
if (method == null) throw new RuntimeException("No such method");
method.setAccessible(true);
try {
return (T) method.invoke(instance, paramaters);
} catch (InvocationTargetException ex) {
throw new RuntimeException(ex.getCause());
} catch (Exception ex) {
throw new RuntimeException(ex);
}
}
private static <T> Constructor<T> makeConstructor(Class<?> clazz, Class<?>... paramaterTypes) {
try {
return (Constructor<T>) clazz.getConstructor(paramaterTypes);
} catch (NoSuchMethodException ex) {
return null;
} catch (Exception ex) {
throw new RuntimeException(ex);
}
}
private static <T> T callConstructor(Constructor<T> constructor, Object... paramaters) {
if (constructor == null) throw new RuntimeException("No such constructor");
constructor.setAccessible(true);
try {
return (T) constructor.newInstance(paramaters);
} catch (InvocationTargetException ex) {
throw new RuntimeException(ex.getCause());
} catch (Exception ex) {
throw new RuntimeException(ex);
}
}
} 