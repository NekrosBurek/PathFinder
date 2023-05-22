package de.cubbossa.pathfinder.command.impl;

import de.cubbossa.pathapi.PathFinder;
import de.cubbossa.pathapi.misc.NamespacedKey;
import de.cubbossa.pathapi.visualizer.PathVisualizer;
import de.cubbossa.pathapi.visualizer.VisualizerType;
import de.cubbossa.pathfinder.Messages;
import de.cubbossa.pathfinder.PathPerms;
import de.cubbossa.pathfinder.command.PathFinderSubCommand;
import de.cubbossa.pathfinder.storage.ExamplesLoader;
import de.cubbossa.pathfinder.storage.ExamplesReader;
import de.cubbossa.pathfinder.util.BukkitUtils;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ImportVisualizerCmd extends PathFinderSubCommand {

  public ImportVisualizerCmd(PathFinder pathFinder) {
    super(pathFinder, "importvisualizer");
    withPermission(PathPerms.PERM_CMD_PF_IMPORT_VIS);
    withGeneratedHelp();

    then(new GreedyStringArgument("name")
        .replaceSuggestions((suggestionInfo, suggestionsBuilder) -> {
          return ExamplesLoader.getInstance().getExampleFiles().thenApply(files -> {
            files.stream()
                .map(ExamplesReader.ExampleFile::name)
                .forEach(suggestionsBuilder::suggest);
            suggestionsBuilder.suggest("*");
            return suggestionsBuilder.build();
          });
        })
        .executes((commandSender, objects) -> {
          if (Objects.equals(objects.<String>getUnchecked(0), "*")) {
            ExamplesLoader.getInstance().getExampleFiles().thenAccept(files -> files
                .forEach(exampleFile -> importVisualizer(commandSender, exampleFile)));
            return;
          }
          ExamplesLoader.getInstance().getExampleFiles()
              .thenApply(files -> files.stream().filter(f -> f.name().equalsIgnoreCase(objects.getUnchecked(0))).findFirst())
              .thenCompose(exampleFile -> importVisualizer(commandSender, exampleFile.orElse(null)))
              .exceptionally(throwable -> {
                BukkitUtils.wrap(commandSender).sendMessage(Messages.formatThrowable(throwable));
                return null;
              });
        })
    );
  }

  private CompletableFuture<Void> importVisualizer(CommandSender commandSender, ExamplesReader.ExampleFile exampleFile) {
    if (exampleFile == null) {
      BukkitUtils.wrap(commandSender).sendMessage(Messages.CMD_VIS_IMPORT_NOT_EXISTS);
      return CompletableFuture.completedFuture(null);
    }
    NamespacedKey key = NamespacedKey.fromString(exampleFile.name().replace(".yml", "").replace("$", ":"));
    return getPathfinder().getStorage().loadVisualizer(key).thenCompose(pathVisualizer -> {
      if (pathVisualizer.isPresent()) {
        BukkitUtils.wrap(commandSender).sendMessage(Messages.CMD_VIS_IMPORT_EXISTS);
        return CompletableFuture.completedFuture(null);
      }
      return ExamplesLoader.getInstance()
          .loadVisualizer(exampleFile)
          .thenCompose(v -> getPathfinder().getStorage().saveVisualizer(v).thenApply(unused -> v))
          .thenAccept(visualizer -> {
            BukkitUtils.wrap(commandSender).sendMessage(Messages.CMD_VIS_IMPORT_SUCCESS.formatted(
                TagResolver.resolver("key", Messages.formatKey(key)),
                Placeholder.component("name", visualizer.getDisplayName())
            ));
          })
          .exceptionally(throwable -> {
            BukkitUtils.wrap(commandSender).sendMessage(Messages.formatThrowable(throwable));
            return null;
          });
    });
  }

  private <V extends PathVisualizer<?, ?>> CompletableFuture<V> save(VisualizerType<V> type, V vis) {
    return getPathfinder().getStorage()
        .createAndLoadVisualizer(type, vis.getKey())
        .thenCompose(v -> getPathfinder().getStorage().saveVisualizer(vis).thenApply(u -> v));
  }
}
