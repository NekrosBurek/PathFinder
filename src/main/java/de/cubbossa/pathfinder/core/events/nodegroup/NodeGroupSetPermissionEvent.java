package de.cubbossa.pathfinder.core.events.nodegroup;

import de.cubbossa.pathfinder.core.node.NodeGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class NodeGroupSetPermissionEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final NodeGroup group;
	private @Nullable
	String permission;
	private boolean cancelled;

	public NodeGroupSetPermissionEvent(NodeGroup group, String permission) {
		this.group = group;
		this.permission = permission;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
