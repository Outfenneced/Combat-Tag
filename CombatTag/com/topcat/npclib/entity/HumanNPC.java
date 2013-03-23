package com.topcat.npclib.entity;

import java.util.Arrays;

import net.minecraft.server.v1_5_R2.EntityPlayer;
import net.minecraft.server.v1_5_R2.Packet18ArmAnimation;
import net.minecraft.server.v1_5_R2.Packet5EntityEquipment;
import net.minecraft.server.v1_5_R2.WorldServer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.topcat.npclib.NPCUtils;
import com.topcat.npclib.nms.NPCEntity;

public class HumanNPC extends NPC {
    private net.minecraft.server.v1_5_R2.ItemStack[] previousEquipment = { null, null, null, null, null };

	public HumanNPC(NPCEntity npcEntity) {
		super(npcEntity);
	}

	public void animateArmSwing() {
		((WorldServer) getEntity().world).tracker.a(getEntity(), new Packet18ArmAnimation(getEntity(), 1));
	}

	public void actAsHurt() {
		((WorldServer) getEntity().world).tracker.a(getEntity(), new Packet18ArmAnimation(getEntity(), 2));
	}

	public void setItemInHand(Material m) {
		setItemInHand(m, (short) 0);
	}

	public void setItemInHand(Material m, short damage) {
		((HumanEntity) getEntity().getBukkitEntity()).setItemInHand(new ItemStack(m, 1, damage));
	}

	public void setName(String name) {
		((NPCEntity) getEntity()).name = name;
	}

	public String getName() {
		return ((NPCEntity) getEntity()).name;
	}

	public PlayerInventory getInventory() {
		return ((HumanEntity) getEntity().getBukkitEntity()).getInventory();
	}

	public void updateEquipment() {
	    int changes = 0;
	    
        for (int i = 0; i < previousEquipment.length; i++) {
            net.minecraft.server.v1_5_R2.ItemStack previous = previousEquipment[i];
            net.minecraft.server.v1_5_R2.ItemStack current = ((EntityPlayer)getEntity()).getEquipment(i);
            if (current == null) { continue; }
            
            if (!net.minecraft.server.v1_5_R2.ItemStack.equals(previous, current) || (previous != null && !previous.equals(current))) {
                NPCUtils.sendPacketNearby(getBukkitEntity().getLocation(), new Packet5EntityEquipment(getEntity().id, i, current));
                ++changes;
            }
        }
        
        if (changes > 0) {
            previousEquipment = Arrays.copyOf(((EntityPlayer)getEntity()).getEquipment(), previousEquipment.length);
        }
    }

	public void putInBed(Location bed) {
		getEntity().setPosition(bed.getX(), bed.getY(), bed.getZ());
		getEntity().a((int) bed.getX(), (int) bed.getY(), (int) bed.getZ());
	}

	public void getOutOfBed() {
		((NPCEntity) getEntity()).a(true, true, true);
	}

	public void setSneaking() {
		getEntity().setSneaking(true);
	}

	public void lookAtPoint(Location point) {
		if (getEntity().getBukkitEntity().getWorld() != point.getWorld()) {
			return;
		}
		Location npcLoc = ((LivingEntity) getEntity().getBukkitEntity()).getEyeLocation();
		double xDiff = point.getX() - npcLoc.getX();
		double yDiff = point.getY() - npcLoc.getY();
		double zDiff = point.getZ() - npcLoc.getZ();
		double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
		double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
		double newYaw = Math.acos(xDiff / DistanceXZ) * 180 / Math.PI;
		double newPitch = Math.acos(yDiff / DistanceY) * 180 / Math.PI - 90;
		if (zDiff < 0.0) {
			newYaw = newYaw + Math.abs(180 - newYaw) * 2;
		}
		getEntity().yaw = (float) (newYaw - 90);
		getEntity().pitch = (float) newPitch;
		((EntityPlayer)getEntity()).bU = (float)(newYaw - 90);
	}

}