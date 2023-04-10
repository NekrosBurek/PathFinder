package de.cubbossa.pathfinder.core.node.implementation;

import de.cubbossa.pathfinder.PathPlugin;
import de.cubbossa.pathfinder.core.node.Edge;
import de.cubbossa.pathfinder.core.node.Node;
import de.cubbossa.pathfinder.core.node.NodeType;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EmptyNode implements Node<EmptyNode> {

  public static final NodeType<EmptyNode> TYPE = new NodeType<>(
      new NamespacedKey(PathPlugin.getInstance(), "empty"),
      "empty",
      new ItemStack(Material.DIRT),
      PathPlugin.getInstance().getMiniMessage()
  ) {

    @Override
    public EmptyNode createAndLoadNode(Context context) {
      throw new IllegalStateException("EmptyNode are only part of runtime navigation and "
          + "must be created from constructor.");
    }
  };

  private final UUID uuid = UUID.randomUUID();
  @Getter
  private final Location location;

  public EmptyNode(World world) {
    this.location = new Location(world, 0, 0, 0);
  }

  @Override
  public NodeType<EmptyNode> getType() {
    return TYPE;
  }

  @Override
  public UUID getNodeId() {
    return uuid;
  }

  @Override
  public void setLocation(Location location) {

  }

  @Override
  public Collection<Edge> getEdges() {
    return new HashSet<>();
  }

  @Override
  public int compareTo(@NotNull Node o) {
    return 0;
  }

  @Override
  public String toString() {
    return "EmptyNode{}";
  }
}
