package de.cubbossa.pathfinder.core.events.nodegroup;

import de.cubbossa.pathfinder.core.node.NodeGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
@AllArgsConstructor
public class NodeGroupSetNameEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final NodeGroup group;
	private String nameFormat;
	private boolean cancelled;

	public NodeGroupSetNameEvent(NodeGroup group, String nameFormat) {
		this.group = group;
		this.nameFormat = nameFormat;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
