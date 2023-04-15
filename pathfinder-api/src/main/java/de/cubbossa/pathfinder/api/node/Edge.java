package de.cubbossa.pathfinder.api.node;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Edge {

  UUID getStart();
  UUID getEnd();
  float getWeight();
  CompletableFuture<Node<?>> resolveStart();
  CompletableFuture<Node<?>> resolveEnd();
}
