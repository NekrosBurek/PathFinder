package de.cubbossa.pathfinder.core.commands;

import de.cubbossa.pathfinder.Messages;
import de.cubbossa.pathfinder.PathFinderAPI;
import de.cubbossa.pathfinder.PathPlugin;
import de.cubbossa.pathfinder.PathPluginExtension;
import de.cubbossa.pathfinder.core.node.NodeHandler;
import de.cubbossa.pathfinder.core.nodegroup.modifier.DiscoverableModifier;
import de.cubbossa.pathfinder.data.DataExporter;
import de.cubbossa.pathfinder.data.ApplicationLayer;
import de.cubbossa.pathfinder.data.SqliteDataStorage;
import de.cubbossa.pathfinder.data.YmlDataStorage;
import de.cubbossa.pathfinder.module.discovering.DiscoverHandler;
import de.cubbossa.pathfinder.module.visualizing.command.VisualizerImportCommand;
import de.cubbossa.serializedeffects.EffectHandler;
import de.cubbossa.translations.TranslationHandler;
import dev.jorel.commandapi.arguments.TextArgument;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * The basic command of this plugin, which handles things like reload, export, import, etc.
 */
public class PathFinderCommand extends Command {

  /**
   * The basic command of this plugin, which handles things like reload, export, import, etc.
   */
  public PathFinderCommand() {
    super("pathfinder");
    withAliases("pf");

    withRequirement(sender ->
        sender.hasPermission(PathPlugin.PERM_CMD_PF_HELP)
            || sender.hasPermission(PathPlugin.PERM_CMD_PF_INFO)
            || sender.hasPermission(PathPlugin.PERM_CMD_PF_IMPORT)
            || sender.hasPermission(PathPlugin.PERM_CMD_PF_EXPORT)
            || sender.hasPermission(PathPlugin.PERM_CMD_PF_RELOAD)
    );

    executes((sender, args) -> {
      TranslationHandler.getInstance().sendMessage(Messages.HELP.format(
          Placeholder.parsed("version", PathPlugin.getInstance().getDescription().getVersion())
      ), sender);
    });

    then(CustomArgs.literal("info")
        .withPermission(PathPlugin.PERM_CMD_PF_INFO)
        .executes((commandSender, objects) -> {
          PluginDescriptionFile desc = PathPlugin.getInstance().getDescription();
          TranslationHandler.getInstance().sendMessage(Messages.INFO.format(TagResolver.builder()
              .resolver(Placeholder.unparsed("version", desc.getVersion()))
              .resolver(Placeholder.unparsed("api-version",
                  desc.getAPIVersion() == null ? "none" : desc.getAPIVersion()))
              .resolver(Placeholder.unparsed("authors", String.join(",", desc.getAuthors())))
              .build()), commandSender);
        }));

    then(CustomArgs.literal("modules")
        .withPermission(PathPlugin.PERM_CMD_PF_MODULES)
        .executes((commandSender, args) -> {
          List<String> list = PathPlugin.getInstance().getExtensions().stream()
              .map(PathPluginExtension::getKey)
              .map(NamespacedKey::toString).toList();

          TranslationHandler.getInstance().sendMessage(Messages.MODULES.format(TagResolver.builder()
              .resolver(TagResolver.resolver("modules", Messages.formatList(list, Component::text)))
              .build()), commandSender);
        }));

    then(CustomArgs.literal("editmode")
        .executesPlayer((commandSender, args) -> {
          NodeHandler.getInstance().toggleNodeGroupEditor(commandSender, NodeHandler.GROUP_GLOBAL);
        })
        .then(CustomArgs.nodeGroupArgument("group"))
            .executesPlayer((commandSender, args) -> {
              NodeHandler.getInstance().toggleNodeGroupEditor(commandSender, (NamespacedKey) args[0]);
            }));

    then(CustomArgs.literal("help")
        .withPermission(PathPlugin.PERM_CMD_PF_HELP)
        .executes((commandSender, objects) -> {
          TranslationHandler.getInstance().sendMessage(Messages.CMD_HELP, commandSender);
        }));

    then(CustomArgs.literal("export")
        .withPermission(PathPlugin.PERM_CMD_PF_EXPORT)
        .then(CustomArgs.literal("sqlite")
            .then(new TextArgument("filename")
                .executes((commandSender, objects) -> {
                  PathPlugin pl = PathPlugin.getInstance();
                  long now = System.currentTimeMillis();
                  Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
                    String fileName = (String) objects[0];
                    if (!fileName.endsWith(".db")) {
                      fileName = fileName + ".db";
                    }
                    ApplicationLayer storage =
                        new SqliteDataStorage(new File(pl.getDataFolder(), "exports/" + fileName));
                    try {
                      storage.connect();
                      DataExporter.all().save(storage);
                    } catch (IOException e) {
                      pl.getLogger().log(Level.SEVERE, e, e::getMessage);
                      commandSender.sendMessage(
                          "Complete in " + (System.currentTimeMillis() - now) + "ms.");
                    }
                  });
                })
            )
        )
        .then(CustomArgs.literal("yaml")
            .then(new TextArgument("directory")
                .executes((commandSender, objects) -> {
                  PathPlugin pl = PathPlugin.getInstance();
                  long now = System.currentTimeMillis();
                  Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
                    String dir = (String) objects[0];
                    if (!dir.startsWith("/")) {
                      dir = "/" + dir;
                    }
                    File directory = new File(pl.getDataFolder(), "exports/" + dir);
                    directory.mkdirs();
                    ApplicationLayer storage = new YmlDataStorage(directory);
                    try {
                      storage.connect();
                      DataExporter.all().save(storage);
                      commandSender.sendMessage(
                          "Complete in " + (System.currentTimeMillis() - now) + "ms.");
                    } catch (IOException e) {
                      pl.getLogger().log(Level.SEVERE, e, e::getMessage);
                    }
                  });
                })
            )
        )
    );

    then(CustomArgs.literal("import")
        .withPermission(PathPlugin.PERM_CMD_PF_IMPORT)
        .then(new VisualizerImportCommand("visualizer", 0))
    );

    then(CustomArgs.literal("reload")
        .withPermission(PathPlugin.PERM_CMD_PF_RELOAD)

        .executes((sender, objects) -> {
          long now = System.currentTimeMillis();

          CompletableFuture.runAsync(() -> {
            try {
              TranslationHandler.getInstance().registerAnnotatedLanguageClass(Messages.class);
              TranslationHandler.getInstance().loadStyle();
              TranslationHandler.getInstance().loadLanguages();

              EffectHandler.getInstance().clearCache(PathPlugin.getInstance().getEffectsFile());


            } catch (Throwable t) {
              throw new RuntimeException(t);
            }
          }).whenComplete((unused, throwable) -> {
            if (throwable != null) {
              TranslationHandler.getInstance()
                  .sendMessage(Messages.RELOAD_ERROR.format(TagResolver.builder()
                      .resolver(Placeholder.component("error", Component.text(throwable.getMessage()
                          .replaceFirst("java\\.lang\\.RuntimeException: [^:]*: ", ""))))
                      .build()), sender);
              PathPlugin.getInstance().getLogger()
                  .log(Level.SEVERE, "Error occured while reloading files: ", throwable);
            } else {
              TranslationHandler.getInstance()
                  .sendMessage(Messages.RELOAD_SUCCESS.format(TagResolver.builder()
                      .resolver(Placeholder.unparsed("ms", System.currentTimeMillis() - now + ""))
                      .build()), sender);
            }
          });
        })

        .then(CustomArgs.literal("language")
            .executes((sender, objects) -> {
              long now = System.currentTimeMillis();

              CompletableFuture.runAsync(() -> {
                try {
                  TranslationHandler.getInstance().registerAnnotatedLanguageClass(Messages.class);
                  TranslationHandler.getInstance().loadStyle();
                  TranslationHandler.getInstance().loadLanguages();
                } catch (Throwable t) {
                  throw new RuntimeException(t);
                }
              }).whenComplete((unused, throwable) -> {
                if (throwable != null) {
                  TranslationHandler.getInstance()
                      .sendMessage(Messages.RELOAD_ERROR.format(TagResolver.builder()
                          .resolver(Placeholder.component("error", Component.text(
                              throwable.getMessage()
                                  .replaceFirst("java\\.lang\\.RuntimeException: [^:]*: ", ""))))
                          .build()), sender);
                  PathPlugin.getInstance().getLogger()
                      .log(Level.SEVERE, "Error occured while reloading files: ", throwable);
                } else {
                  TranslationHandler.getInstance()
                      .sendMessage(Messages.RELOAD_SUCCESS_LANG.format(TagResolver.builder()
                          .resolver(
                              Placeholder.unparsed("ms", System.currentTimeMillis() - now + ""))
                          .build()), sender);
                }
              });
            })
        )

        .then(CustomArgs.literal("effects")
            .executes((sender, objects) -> {
              long now = System.currentTimeMillis();

              CompletableFuture.runAsync(() -> {
                try {
                  PathPlugin.getInstance().generateIfAbsent("effects.nbo");
                  EffectHandler.getInstance().clearCache(PathPlugin.getInstance().getEffectsFile());
                } catch (Throwable t) {
                  throw new RuntimeException(t);
                }
              }).whenComplete((unused, throwable) -> {
                if (throwable != null) {
                  TranslationHandler.getInstance()
                      .sendMessage(Messages.RELOAD_ERROR.format(TagResolver.builder()
                          .resolver(Placeholder.component("error", Component.text(
                              throwable.getMessage()
                                  .replaceFirst("java\\.lang\\.RuntimeException: [^:]*: ", ""))))
                          .build()), sender);
                  PathPlugin.getInstance().getLogger()
                      .log(Level.SEVERE, "Error occured while reloading files: ", throwable);
                } else {
                  TranslationHandler.getInstance()
                      .sendMessage(Messages.RELOAD_SUCCESS_FX.format(TagResolver.builder()
                          .resolver(
                              Placeholder.unparsed("ms", System.currentTimeMillis() - now + ""))
                          .build()), sender);
                }
              });
            })
        )
        .then(CustomArgs.literal("config")
            .executes((sender, objects) -> {
              long now = System.currentTimeMillis();

              CompletableFuture.runAsync(() -> {
                try {
                  PathPlugin.getInstance().loadConfig();
                } catch (Throwable t) {
                  throw new RuntimeException(t);
                }
              }).whenComplete((unused, throwable) -> {
                if (throwable != null) {
                  TranslationHandler.getInstance()
                      .sendMessage(Messages.RELOAD_ERROR.format(TagResolver.builder()
                          .resolver(Placeholder.component("error", Component.text(
                              throwable.getMessage()
                                  .replaceFirst("java\\.lang\\.RuntimeException: [^:]*: ", ""))))
                          .build()), sender);
                  PathPlugin.getInstance().getLogger()
                      .log(Level.SEVERE, "Error occured while reloading configuration: ",
                          throwable);
                } else {
                  TranslationHandler.getInstance()
                      .sendMessage(Messages.RELOAD_SUCCESS_CFG.format(TagResolver.builder()
                          .resolver(
                              Placeholder.unparsed("ms", System.currentTimeMillis() - now + ""))
                          .build()), sender);
                }
              });
            })
        )
    );

    then(CustomArgs.literal("forcefind")
            .withGeneratedHelp()
            .withPermission(PathPlugin.PERM_CMD_PF_FORCEFIND)
                    .then(CustomArgs.player("player")
                            .withGeneratedHelp()
                            .then(CustomArgs.discoverableArgument("discovering")
                                    .executes((commandSender, args) -> {
                                      onForceFind(commandSender, (Player) args[1], (NamespacedKey) args[2]);
                                    }))));
    then(CustomArgs.literal("forceforget")
            .withGeneratedHelp()
            .withPermission(PathPlugin.PERM_CMD_PF_FORCEFORGET)
                    .then(CustomArgs.player("player")
                            .withGeneratedHelp()
                            .then(CustomArgs.discoverableArgument("discovering")
                                    .executes((commandSender, args) -> {
                                      onForceForget(commandSender, (Player) args[1], (NamespacedKey) args[2]);
                                    }))));
  }

  private void onForceFind(CommandSender sender, Player target, NamespacedKey discoverable) {

    PathFinderAPI.builder().getNodeGroup(discoverable).thenAccept(group -> {
      DiscoverableModifier mod = group.getModifier(DiscoverableModifier.class);

      DiscoverHandler.getInstance().discover(target.getUniqueId(), group, LocalDateTime.now());

      TranslationHandler.getInstance()
          .sendMessage(Messages.CMD_RM_FORCE_FIND.format(TagResolver.builder()
              .resolver(Placeholder.component("name",
                  PathPlugin.getInstance().getAudiences().player(target)
                      .getOrDefault(Identity.DISPLAY_NAME, Component.text(target.getName()))))
              .tag("discovery", Tag.inserting(mod.getDisplayName())).build()), sender);
    });
  }

  private void onForceForget(CommandSender sender, Player target, NamespacedKey discoverable) {
    PathFinderAPI.builder().getNodeGroup(discoverable).thenAccept(group -> {
      DiscoverableModifier mod = group.getModifier(DiscoverableModifier.class);

      DiscoverHandler.getInstance().forget(target.getUniqueId(), group);

      TranslationHandler.getInstance()
          .sendMessage(Messages.CMD_RM_FORCE_FORGET.format(TagResolver.builder()
              .resolver(Placeholder.component("name",
                  PathPlugin.getInstance().getAudiences().player(target)
                      .getOrDefault(Identity.DISPLAY_NAME, Component.text(target.getName()))))
              .tag("discovery", Tag.inserting(mod.getDisplayName())).build()), sender);
    });
  }
}
