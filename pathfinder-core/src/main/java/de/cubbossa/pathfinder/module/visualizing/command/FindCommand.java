package de.cubbossa.pathfinder.module.visualizing.command;

import de.cubbossa.pathfinder.PathPlugin;
import de.cubbossa.pathfinder.core.commands.Command;
import de.cubbossa.pathfinder.core.commands.CustomArgs;
import de.cubbossa.pathfinder.module.visualizing.FindModule;
import de.cubbossa.pathfinder.util.NodeSelection;
import org.bukkit.Bukkit;

public class FindCommand extends Command {

  public FindCommand() {
    super("find");
    withAliases("gps", "navigate");
    withPermission(PathPlugin.PERM_CMD_FIND);
    withGeneratedHelp();

    then(CustomArgs.navigateSelectionArgument("selection")
        .executesPlayer((player, args) -> {
          Bukkit.getScheduler().runTask(PathPlugin.getInstance(), () -> {
            NodeSelection targets = args.<NodeSelection>getUnchecked(0);
            FindModule.printResult(FindModule.getInstance().findPath(player, targets), player);
          });
        })
    );
  }
}
