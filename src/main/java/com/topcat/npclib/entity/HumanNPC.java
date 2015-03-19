package com.topcat.npclib.entity;

import com.topcat.npclib.NPCUtils;
import com.topcat.npclib.nms.NPCEntity;
import java.util.Arrays;
import net.minecraft.server.v1_8_R2.Packet;
import net.minecraft.server.v1_8_R2.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R2.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R2.WorldServer;
import net.minecraft.server.v1_8_R2.EntityPlayer;
import net.minecraft.server.v1_8_R2.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
    
    @Override
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
    
    public void sendDespawnPacket() {
        PacketPlayOutPlayerInfo despawnPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, getEntity());
        for (Player player : Bukkit.getOnlinePlayers()) {
            EntityPlayer handle = ((CraftPlayer)player).getHandle();
            handle.playerConnection.sendPacket(despawnPacket);
        }
    }
}
