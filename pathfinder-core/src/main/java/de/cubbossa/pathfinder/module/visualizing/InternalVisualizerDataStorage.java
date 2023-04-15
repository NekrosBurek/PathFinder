package de.cubbossa.pathfinder.module.visualizing;

import de.cubbossa.pathfinder.api.misc.NamespacedKey;
import de.cubbossa.pathfinder.api.visualizer.PathVisualizer;
import de.cubbossa.pathfinder.storage.Storage;
import de.cubbossa.pathfinder.api.storage.VisualizerDataStorage;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InternalVisualizerDataStorage<T extends PathVisualizer<T, ?, ?>>
    implements VisualizerDataStorage<T> {

  private final AbstractVisualizerType<T> type;
  private final Storage storage;

  @Override
  public T createAndLoadVisualizer(NamespacedKey key) {
    return storage.getImplementation().createAndLoadVisualizer(type, key);
  }

  @Override
  public Map<NamespacedKey, T> loadVisualizers() {
    return storage.getImplementation().loadVisualizers(type);
  }

  @Override
  public Optional<T> loadVisualizer(NamespacedKey key) {
    return storage.getImplementation().loadVisualizer(type, key);
  }

  @Override
  public void saveVisualizer(T visualizer) {
    storage.getImplementation().saveVisualizer(visualizer);
  }

  @Override
  public void deleteVisualizer(T visualizer) {
    storage.getImplementation().deleteVisualizer(visualizer);
  }
}
