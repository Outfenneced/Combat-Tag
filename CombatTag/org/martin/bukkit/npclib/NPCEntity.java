package org.martin.bukkit.npclib;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.entity.HumanEntity;
import org.martin.bukkit.npclib.NPCPath.Node;

/**
 *
 * @author martin
 */
public class NPCEntity extends EntityPlayer {

    private int lastTargetId;
    private long lastBounceTick;
    private int lastBounceId;
    private NPCPath path;
    private Node last;
    private Timer timer = new Timer();
    private Location end;
    private int maxIter;

    public void pathFindTo(Location l, int maxIterations) {
        path = new NPCPath(getBukkitEntity().getLocation(), l, maxIterations);
        end = l;
        maxIter = maxIterations;
        timer.schedule(new movePath(), 300);
    }

    public class movePath extends TimerTask {

        @Override
        public void run() {
            if (path != null) {
                Node n = path.getNextNode();
                Block b = null;
                float angle = yaw;
                float look = pitch;
                if (n != null) {
                    if (last == null || path.checkPath(n, last, true)) {
                        b = n.b;
                        if (last != null) {
                            angle = ((float) Math.toDegrees(Math.atan2(last.b.getX() - b.getX(), last.b.getZ() - b.getZ())));
                            look = (float) (Math.toDegrees(Math.asin(last.b.getY() - b.getY())) / 2);
                        }
                        setPositionRotation(b.getX() + 0.5, b.getY(), b.getZ() + 0.5, angle, look);
                        timer.schedule(new movePath(), 300);
                    } else {
                        pathFindTo(end, maxIter);
                    }
                } else if (last != null) {
                    setPositionRotation(end.getX(), end.getY(), end.getZ(), end.getYaw(), end.getPitch());
                }
                last = n;
            }
        }
    }

    public NPCEntity(MinecraftServer minecraftserver, World world, String s, ItemInWorldManager iteminworldmanager) {
        super(minecraftserver, world, s, iteminworldmanager);
        NetworkManager netMgr = new NPCNetworkManager(new NullSocket(), "NPC Manager", new NetHandler() {

            @Override
            public boolean c() {
                return true;
            }
        });
        this.netServerHandler = new NPCNetHandler(minecraftserver, netMgr, this);
        this.lastTargetId = -1;
        this.lastBounceId = -1;
        this.lastBounceTick = 0;
    }

    public void animateArmSwing() {
        ((WorldServer)this.world).tracker.a(this, new Packet18ArmAnimation(this, 1));
    }

    public void actAsHurt() {
        ((WorldServer)this.world).tracker.a(this, new Packet18ArmAnimation(this, 2));
    }

    @Override
    public boolean b(EntityHuman entity) {

        EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NpcEntityTargetEvent.NpcTargetReason.NPC_RIGHTCLICKED);
        CraftServer server = ((WorldServer) this.world).getServer();
        server.getPluginManager().callEvent(event);

        return super.b(entity);
    }

    @Override
    public void a_(EntityHuman entity) {
        if (lastTargetId == -1 || lastTargetId != entity.id) {
            EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NpcEntityTargetEvent.NpcTargetReason.CLOSEST_PLAYER);
            CraftServer server = ((WorldServer) this.world).getServer();
            server.getPluginManager().callEvent(event);
        }
        lastTargetId = entity.id;

        super.a_(entity);
    }

    @Override
    public void c(Entity entity) {
        if (lastBounceId != entity.id || System.currentTimeMillis() - lastBounceTick > 1000) {
            EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NpcEntityTargetEvent.NpcTargetReason.NPC_BOUNCED);
            CraftServer server = ((WorldServer) this.world).getServer();
            server.getPluginManager().callEvent(event);

            lastBounceTick = System.currentTimeMillis();
        }

        lastBounceId = entity.id;

        super.c(entity);
    }

    public PlayerInventory getInventory() {
    	return ((HumanEntity) getBukkitEntity()).getInventory();
    }

    public void setItemInHand(Material m) {
        ((HumanEntity) getBukkitEntity()).setItemInHand(new ItemStack(m, 1));
    }
    
    @Override
    public void move(double arg0, double arg1, double arg2) {
    	setPosition(arg0, arg1, arg2);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
