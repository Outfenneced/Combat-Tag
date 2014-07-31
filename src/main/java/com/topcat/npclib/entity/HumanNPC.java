package com.topcat.npclib.entity;

import net.minecraft.server.v1_7_R4.PacketPlayOutAnimation;
import net.minecraft.server.v1_7_R4.WorldServer;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import com.topcat.npclib.nms.NPCEntity;

public class HumanNPC extends NPC {

    public HumanNPC(NPCEntity npcEntity) {
        super(npcEntity);
    }

    public void actAsHurt() {
        ((WorldServer) getEntity().world).tracker.a(getEntity(), new PacketPlayOutAnimation(getEntity(), 2)); //TESTING
    }

    public void setItemInHand(Material m, short damage) {
        ((HumanEntity) getEntity().getBukkitEntity()).setItemInHand(new ItemStack(m, 1, damage));
    }

    public String getName() {
        return ((NPCEntity) getEntity()).getName(); // CHANGED 1.6.1
    }
}
