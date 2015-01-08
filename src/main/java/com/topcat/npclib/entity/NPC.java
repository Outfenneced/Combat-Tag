package com.topcat.npclib.entity;

import net.minecraft.server.v1_8_R1.Entity;

public class NPC {

    private final Entity entity;

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
}
