package com.topcat.npclib.nms;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.WorldServer;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.entity.EntityTargetEvent;

import com.topcat.npclib.NPCManager;

/**
 *
 * @author martin
 */
public class NPCEntity extends EntityPlayer {

	private int lastTargetId;
	private long lastBounceTick;
	private int lastBounceId;

	public NPCEntity(NPCManager npcManager, BWorld world, String s, ItemInWorldManager itemInWorldManager) {
		super(npcManager.getServer().getMCServer(), world.getWorldServer(), s, itemInWorldManager);

		itemInWorldManager.b(0);

		netServerHandler = new NPCNetHandler(npcManager, this);
		lastTargetId = -1;
		lastBounceId = -1;
		lastBounceTick = 0;
	}

	public void setBukkitEntity(org.bukkit.entity.Entity entity) {
		bukkitEntity = entity;
	}

	@Override
	public boolean b(EntityHuman entity) {
		EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NpcEntityTargetEvent.NpcTargetReason.NPC_RIGHTCLICKED);
		CraftServer server = ((WorldServer) world).getServer();
		server.getPluginManager().callEvent(event);

		return super.b(entity);
	}

	@Override
	public void a_(EntityHuman entity) {
		if (lastTargetId == -1 || lastTargetId != entity.id) {
			EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NpcEntityTargetEvent.NpcTargetReason.CLOSEST_PLAYER);
			CraftServer server = ((WorldServer) world).getServer();
			server.getPluginManager().callEvent(event);
		}
		lastTargetId = entity.id;

		super.a_(entity);
	}

	@Override
	public void c(Entity entity) {
		if (lastBounceId != entity.id || System.currentTimeMillis() - lastBounceTick > 1000) {
			EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NpcEntityTargetEvent.NpcTargetReason.NPC_BOUNCED);
			CraftServer server = ((WorldServer) world).getServer();
			server.getPluginManager().callEvent(event);

			lastBounceTick = System.currentTimeMillis();
		}

		lastBounceId = entity.id;

		super.c(entity);
	}

	@Override
	public void move(double arg0, double arg1, double arg2) {
		setPosition(arg0, arg1, arg2);
	}

}