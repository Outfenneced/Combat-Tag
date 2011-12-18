package com.topcat.npclib.entity;

import com.topcat.npclib.NPCManager;
import com.topcat.npclib.pathing.NPCPath;
import com.topcat.npclib.pathing.NPCPathFinder;
import com.topcat.npclib.pathing.Node;
import com.topcat.npclib.pathing.PathReturn;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import net.minecraft.server.Entity;

public class NPC {
	
	private Entity entity;
	private NPCPathFinder path;
	private Iterator<Node> pathIterator;
	private Node last;
	private NPCPath runningPath;
	private int taskid;
	private Runnable onFail;
	
	public NPC(Entity entity) {
		this.entity = entity;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public void removeFromWorld() {
		try {
			entity.world.removeEntity(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public org.bukkit.entity.Entity getBukkitEntity() {
		return entity.getBukkitEntity();
	}
	
	public void pathFindTo(Location l, PathReturn callback) {
		pathFindTo(l, 3000, callback);
	}
	
	public void pathFindTo(Location l, int maxIterations, PathReturn callback) {
		if (path != null) {
			path.cancel = true;
		}
		path = new NPCPathFinder(getEntity().getBukkitEntity().getLocation(), l, maxIterations, callback);
		path.start();
	}
	
	public void walkTo(Location l) {
		walkTo(l, 3000);
	}
	
	public void walkTo(final Location l, final int maxIterations) {
		pathFindTo(l, maxIterations, new PathReturn() {
			@Override
			public void run(NPCPath path) {
				usePath(path, new Runnable() {
					
					@Override
					public void run() {
						walkTo(l, maxIterations);
					}
				});
			}
		});
	}
	
	public void usePath(NPCPath path) {
		usePath(path, new Runnable() {
			@Override
			public void run() {
				walkTo(runningPath.getEnd(), 3000);
			}
		});
	}
	
	public void usePath(NPCPath path, Runnable onFail) {
		if (taskid == 0) {
			taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(NPCManager.plugin, new Runnable() {
				@Override
				public void run() {
					pathStep();
				}
			}, 6L, 6L);
		}
		pathIterator = path.getPath().iterator();
		runningPath = path;
		this.onFail = onFail;
	}
	
	private void pathStep() {
		if (pathIterator.hasNext()) {
			Node n = pathIterator.next();
			Block b = null;
			float angle = getEntity().yaw;
			float look = getEntity().pitch;
			if (last == null || runningPath.checkPath(n, last, true)) {
				b = n.b;
				if (last != null) {
					angle = ((float) Math.toDegrees(Math.atan2(last.b.getX() - b.getX(), last.b.getZ() - b.getZ())));
					look = (float) (Math.toDegrees(Math.asin(last.b.getY() - b.getY())) / 2);
				}
				getEntity().setPositionRotation(b.getX() + 0.5, b.getY(), b.getZ() + 0.5, angle, look);
			} else {
				onFail.run();
			}
			last = n;
		} else {
			getEntity().setPositionRotation(runningPath.getEnd().getX(), runningPath.getEnd().getY(), runningPath.getEnd().getZ(), runningPath.getEnd().getYaw(), runningPath.getEnd().getPitch());
			Bukkit.getServer().getScheduler().cancelTask(taskid);
			taskid = 0;
		}
	}
	
}