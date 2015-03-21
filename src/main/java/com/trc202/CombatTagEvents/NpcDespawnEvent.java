package com.trc202.CombatTagEvents;

import java.util.UUID;

import net.techcable.npclib.NPC;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.trc202.CombatTag.CombatTag;

public class NpcDespawnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final CombatTag plugin_;
    private final NpcDespawnReason reason_;
    private final UUID playerUUID_;
    private final NPC npc_;

    public NpcDespawnEvent(
            final CombatTag plugin,
            final NpcDespawnReason reason,
            final UUID playerName,
            final NPC npc) {
        plugin_ = plugin;
        reason_ = reason;
        playerUUID_ = playerName;
        npc_ = npc;
    }

    @Override
    public HandlerList getHandlers() {
        return NpcDespawnEvent.getHandlerList();
    }

    public CombatTag getPlugin() {
        return plugin_;
    }

    public NpcDespawnReason getReason() {
        return reason_;
    }

    public UUID getPlayerUUID() {
        return playerUUID_;
    }

    public NPC getNpc() {
        return npc_;
    }
}
