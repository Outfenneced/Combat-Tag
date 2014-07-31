package com.topcat.npclib.nms;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;

public class NpcEntityTargetEvent extends EntityTargetEvent {

    public static enum NpcTargetReason {

        CLOSEST_PLAYER,
        NPC_RIGHTCLICKED,
        NPC_BOUNCED
    }

    public NpcEntityTargetEvent(Entity entity, Entity target, NpcTargetReason reason) {
        super(entity, target, TargetReason.CUSTOM);
    }
}
