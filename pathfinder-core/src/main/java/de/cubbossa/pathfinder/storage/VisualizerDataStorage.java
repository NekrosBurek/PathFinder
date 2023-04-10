package de.cubbossa.pathfinder.storage;

import de.cubbossa.pathfinder.module.visualizing.VisualizerType;
import de.cubbossa.pathfinder.module.visualizing.visualizer.PathVisualizer;
import java.util.Map;
import java.util.Optional;
import org.bukkit.NamespacedKey;

public interface VisualizerDataStorage<T extends PathVisualizer<T, ?>> {

  T createAndLoadVisualizer(NamespacedKey key);
  Map<NamespacedKey, T> loadVisualizers();
  Optional<T> loadVisualizer(NamespacedKey key);
  void saveVisualizer(T visualizer);
  void deleteVisualizer(T visualizer);
}
