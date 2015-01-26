package com.topcat.npclib.entity;

import net.minecraft.server.v1_8_R1.Packet;
import net.minecraft.server.v1_8_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R1.WorldServer;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import com.topcat.npclib.NPCUtils;
import com.topcat.npclib.nms.NPCEntity;

public class HumanNPC extends NPC {

    public HumanNPC(NPCEntity npcEntity) {
        super(npcEntity);
    }

    public void actAsHurt() {
        ((WorldServer) getEntity().world).tracker.a(getEntity(), new PacketPlayOutAnimation(getEntity(), 2)); //TESTING
    }

    public void setItemInHand(Material m, short damage) {
        getBukkitEntity().setItemInHand(new ItemStack(m, 1, damage));
    }

    public String getName() {
        return ((NPCEntity) getEntity()).getName(); // CHANGED 1.6.1
    }
    
    public NPCEntity getEntity() {
        return (NPCEntity) super.getEntity();
    }
    
    public void updateEquipment() {
        Packet[] packets = new Packet[5];
	    for (int i = 0; i < 5; i++) {
	        packets[i] = new PacketPlayOutEntityEquipment(getEntity().getId(), i, getEntity().getEquipment(i));
	    }
	    NPCUtils.sendPacketsNearby(getBukkitEntity().getLocation());
    }
    
    public HumanEntity getBukkitEntity() {
        return (HumanEntity) getEntity().getBukkitEntity();
    }
}
