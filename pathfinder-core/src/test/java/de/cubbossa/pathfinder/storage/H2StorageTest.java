package de.cubbossa.pathfinder.storage;

import de.cubbossa.pathapi.group.ModifierRegistry;
import de.cubbossa.pathapi.node.NodeTypeRegistry;
import de.cubbossa.pathapi.storage.StorageImplementation;
import de.cubbossa.pathapi.visualizer.VisualizerTypeRegistry;

public class H2StorageTest extends StorageTest {

  @Override
  StorageImplementation storage(NodeTypeRegistry registry, ModifierRegistry modifierRegistry,
                                VisualizerTypeRegistry visualizerTypeRegistry) {
    return inMemoryStorage();
  }
}
