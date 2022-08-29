package de.cubbossa.pathfinder.core.commands;

import de.cubbossa.pathfinder.Messages;
import de.cubbossa.pathfinder.PathPlugin;
import de.cubbossa.pathfinder.core.commands.argument.CustomArgs;
import de.cubbossa.pathfinder.core.events.roadmap.RoadmapSelectEvent;
import de.cubbossa.pathfinder.core.node.Discoverable;
import de.cubbossa.pathfinder.core.node.Node;
import de.cubbossa.pathfinder.core.node.NodeGroupHandler;
import de.cubbossa.pathfinder.core.roadmap.RoadMap;
import de.cubbossa.pathfinder.core.roadmap.RoadMapEditor;
import de.cubbossa.pathfinder.core.roadmap.RoadMapHandler;
import de.cubbossa.pathfinder.data.PathPlayer;
import de.cubbossa.pathfinder.data.PathPlayerHandler;
import de.cubbossa.pathfinder.module.discovering.DiscoverHandler;
import de.cubbossa.pathfinder.module.visualizing.visualizer.PathVisualizer;
import de.cubbossa.pathfinder.util.CommandUtils;
import de.cubbossa.pathfinder.util.NodeSelection;
import de.cubbossa.translations.FormattedMessage;
import de.cubbossa.translations.TranslationHandler;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.function.Predicate;

public class RoadMapCommand extends CommandTree implements Listener {

	public RoadMapCommand() {
		super("roadmap");
		withPermission(PathPlugin.PERM_CMD_RM);

		// to update the roadmap selected requirement
		Bukkit.getPluginManager().registerEvents(this, PathPlugin.getInstance());

		withAliases("rm");

		then(new LiteralArgument("info")
				.withPermission(PathPlugin.PERM_CMD_RM_INFO)
				.executes((commandSender, objects) -> {
					onInfo(commandSender, null);
				})
				.then(CustomArgs.roadMapArgument("roadmap")
						.executes((commandSender, args) -> {
							onInfo(commandSender, (RoadMap) args[0]);
						})));

		then(new LiteralArgument("create")
				.withPermission(PathPlugin.PERM_CMD_RM_CREATE)
				.then(new StringArgument("key")
						.executesPlayer((player, args) -> {
							onCreate(player, new NamespacedKey(PathPlugin.getInstance(), (String) args[0]), player.getWorld(), false);
						})
						.then(CustomArgs.worldArgument("world")
								.executes((player, args) -> {
									onCreate(player, (NamespacedKey) args[0], (World) args[1], false);
								})
								.then(new LiteralArgument("findable")
										.executes((player, args) -> {
											onCreate(player, (NamespacedKey) args[0], (World) args[1], true);
										})))));

		then(new LiteralArgument("delete")
				.withPermission(PathPlugin.PERM_CMD_RM_DELETE)
				.then(CustomArgs.roadMapArgument("roadmap")
						.executes((commandSender, args) -> {
							onDelete(commandSender, (RoadMap) args[0]);
						})));

		then(new LiteralArgument("editmode")
				.withRequirement(hasRoadMapSelected())
				.withPermission(PathPlugin.PERM_CMD_RM_EDITMODE)
				.executesPlayer((player, args) -> {
					onEdit(player, null);
				})
				.then(CustomArgs.roadMapArgument("roadmap")
						.executesPlayer((player, args) -> {
							onEdit(player, (RoadMap) args[0]);
						})));

		then(new LiteralArgument("list")
				.withPermission(PathPlugin.PERM_CMD_RM_LIST)
				.executes((commandSender, args) -> {
					onList(commandSender, 0);
				})
				.then(new IntegerArgument("page", 1)
						.executes((commandSender, args) -> {
							onList(commandSender, (Integer) args[0]);
						})));

		then(new LiteralArgument("forcefind")
				.withPermission(PathPlugin.PERM_CMD_RM_FORCEFIND)
				.then(CustomArgs.roadMapArgument("roadmap")
						.then(new PlayerArgument("player")
								.then(CustomArgs.nodeSelectionArgument("selection")
										.executes((commandSender, args) -> {
											onForceFind(commandSender, (RoadMap) args[0], (Player) args[1], (NodeSelection) args[2]);
										})))));
		then(new LiteralArgument("forceforget")
				.withPermission(PathPlugin.PERM_CMD_RM_FORCEFORGET)
				.then(CustomArgs.roadMapArgument("roadmap")
						.then(new PlayerArgument("player")
								.then(CustomArgs.nodeSelectionArgument("selection")
										.executes((commandSender, args) -> {
											onForceForget(commandSender, (RoadMap) args[0], (Player) args[1], (NodeSelection) args[2]);
										})))));

		then(new LiteralArgument("select")
				.withPermission(PathPlugin.PERM_CMD_RM_SELECT)
				.then(CustomArgs.roadMapArgument("roadmap")
						.executes((commandSender, objects) -> {
							onSelect(commandSender, (RoadMap) objects[0]);
						})));
		then(new LiteralArgument("deselect")
				.withRequirement(hasRoadMapSelected())
				.withPermission(PathPlugin.PERM_CMD_RM_DESELECT)
				.executes((commandSender, args) -> {
					onDeselect(commandSender);
				}));

		then(new LiteralArgument("set")
				.withRequirement(hasRoadMapSelected())
				.then(new LiteralArgument("visualizer")
						.withPermission(PathPlugin.PERM_CMD_RM_SET_VIS)
						.then(CustomArgs.pathVisualizerArgument("visualizer")
								.executes((commandSender, args) -> {
									onStyle(commandSender, (PathVisualizer) args[0]);
								})))

				.then(new LiteralArgument("name")
						.withPermission(PathPlugin.PERM_CMD_RM_SET_NAME)
						.then(CustomArgs.miniMessageArgument("name")
								.executes((commandSender, args) -> {
									onRename(commandSender, (String) args[0]);
								})))

				.then(new LiteralArgument("world")
						.withPermission(PathPlugin.PERM_CMD_RM_SET_WORLD)
						.then(CustomArgs.worldArgument("world")
								.executes((commandSender, objects) -> {
									onChangeWorld(commandSender, (World) objects[0], false);
								})
								.then(new LiteralArgument("force")
										.executes((commandSender, args) -> {
											onChangeWorld(commandSender, (World) args[0], true);
										}))))
				.then(new LiteralArgument("find-distance")
						.withPermission(PathPlugin.PERM_CMD_RM_SET_FIND_DIST)
						.then(new DoubleArgument("distance", 0.01)
								.executes((commandSender, args) -> {
									onFindDistance(commandSender, (Double) args[0]);
								})))
				.then(new LiteralArgument("findable")
						.withPermission(PathPlugin.PERM_CMD_NG_SET_FINDABLE)
						.then(new BooleanArgument("findable")
								.executes((commandSender, args) -> {
									onSetFindable(commandSender, (Boolean) args[0]);
								})))
				.then(new LiteralArgument("curve-length")
						.withPermission(PathPlugin.PERM_CMD_RM_SET_CURVE)
						.then(new DoubleArgument("curvelength", 0)
								.executes((commandSender, args) -> {
									onChangeTangentStrength(commandSender, null, (Double) args[0]);
								}))));

		then(new LiteralArgument("edit")
				.then(CustomArgs.roadMapArgument("roadmap")
						.then(new LiteralArgument("visualizer")
								.withPermission(PathPlugin.PERM_CMD_RM_SET_VIS)
								.then(CustomArgs.pathVisualizerArgument("visualizer")
										.executes((commandSender, args) -> {
											onStyle(commandSender, (PathVisualizer) args[1]);
										})))

						.then(new LiteralArgument("name")
								.withPermission(PathPlugin.PERM_CMD_RM_SET_NAME)
								.then(CustomArgs.miniMessageArgument("name")
										.executes((commandSender, args) -> {
											onRename(commandSender, (String) args[1]);
										})))

						.then(new LiteralArgument("world")
								.withPermission(PathPlugin.PERM_CMD_RM_SET_WORLD)
								.then(CustomArgs.worldArgument("world")
										.executes((commandSender, objects) -> {
											onChangeWorld(commandSender, (World) objects[1], false);
										})
										.then(new LiteralArgument("force")
												.executes((commandSender, args) -> {
													onChangeWorld(commandSender, (World) args[1], true);
												}))))
						.then(new LiteralArgument("find-distance")
								.withPermission(PathPlugin.PERM_CMD_RM_SET_FIND_DIST)
								.then(new DoubleArgument("distance", 0.01)
										.executes((commandSender, args) -> {
											onFindDistance(commandSender, (Double) args[1]);
										})))
						.then(new LiteralArgument("findable")
								.withPermission(PathPlugin.PERM_CMD_RM_SET_FINDABLE)
								.then(new BooleanArgument("findable")
										.executes((commandSender, args) -> {
											onSetFindable(commandSender, (Boolean) args[1]);
										})))
						.then(new LiteralArgument("curve-length")
								.withPermission(PathPlugin.PERM_CMD_RM_SET_CURVE)
								.then(new DoubleArgument("curvelength", 0)
										.executes((commandSender, args) -> {
											onChangeTangentStrength(commandSender, (RoadMap) args[0], (Double) args[1]);
										})))));
	}

	private Predicate<CommandSender> hasRoadMapSelected() {
		return sender -> PathPlayerHandler.getInstance().getPlayer(sender).getSelectedRoadMap() != null;
	}

	public void onInfo(CommandSender sender, @Nullable RoadMap roadMap) throws WrapperCommandSyntaxException {
		if (roadMap == null) {
			roadMap = CustomArgs.resolveRoadMapWrappedException(sender);
		}

		FormattedMessage message = Messages.CMD_RM_INFO.format(TagResolver.builder()
				.tag("id", Tag.preProcessParsed(roadMap.getKey() + ""))
				.tag("name", Tag.inserting(roadMap.getDisplayName()))
				.tag("name-format", Tag.inserting(Component.text(roadMap.getNameFormat())))
				.tag("nodes", Tag.inserting(Messages.formatNodeSelection(sender, roadMap.getNodes())))
				.tag("groups", Tag.inserting(Messages.formatNodeGroups(sender, NodeGroupHandler.getInstance().getNodeGroups(roadMap))))
				.tag("world", Tag.preProcessParsed(roadMap.getWorld().getName()))
				.tag("discoverable", Tag.inserting(roadMap.isFindableNodes() ?
						Messages.GEN_TRUE.asComponent(sender) : Messages.GEN_FALSE.asComponent(sender)))
				.tag("find-distance", Tag.preProcessParsed(roadMap.getNodeFindDistance() + ""))
				.tag("curve-length", Tag.preProcessParsed(roadMap.getDefaultBezierTangentLength() + ""))
				.tag("path-visualizer", Tag.inserting(roadMap.getVisualizer() == null ? Messages.GEN_NULL.asComponent() : roadMap.getVisualizer().getDisplayName()))
				.build());

		TranslationHandler.getInstance().sendMessage(message, sender);
	}


	public void onCreate(CommandSender sender, NamespacedKey key, World world, boolean findableNodes) {

		try {
			RoadMap roadMap = RoadMapHandler.getInstance().createRoadMap(key, world, findableNodes);
			TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_CREATE_SUCCESS
					.format(TagResolver.resolver("name", Tag.inserting(roadMap.getDisplayName()))), sender);

			PathPlayer pathPlayer = PathPlayerHandler.getInstance().getPlayer(sender);
			pathPlayer.setSelectedRoadMap(roadMap.getKey());

		} catch (Exception e) {
			TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_CREATE_FAIL, sender);
			e.printStackTrace();
		}
	}

	public void onDelete(CommandSender sender, @Nullable RoadMap roadMap) throws WrapperCommandSyntaxException {
		if (roadMap == null) {
			roadMap = CustomArgs.resolveRoadMapWrappedException(sender);
		}

		RoadMapHandler.getInstance().deleteRoadMap(roadMap);
		TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_DELETE.format(TagResolver.resolver("roadmap", Tag.inserting(roadMap.getDisplayName()))), sender);
	}

	public void onEdit(Player player, @Nullable RoadMap roadMap) {
		PathPlayer pp = PathPlayerHandler.getInstance().getPlayer(player);
		if (pp == null) {
			return;
		}
		if (roadMap == null) {
			if (RoadMapHandler.getInstance().getRoadMaps().size() == 0) {
				TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_EM_CREATE, player);
				return;
			}
			if (pp.getSelectedRoadMap() != null) {
				roadMap = RoadMapHandler.getInstance().getRoadMap(pp.getSelectedRoadMap());
			}
		}
		if (roadMap == null) {
			if (RoadMapHandler.getInstance().getRoadMaps().size() == 1) {
				roadMap = RoadMapHandler.getInstance().getRoadMaps().values().stream().findAny().orElse(null);
			} else {
				Collection<RoadMap> inWorld = RoadMapHandler.getInstance().getRoadMaps(player.getWorld());
				if (inWorld.size() == 1) {
					roadMap = inWorld.stream().findAny().orElse(null);
				}
			}
			if (roadMap != null) {
				pp.setSelectedRoadMap(roadMap.getKey());
				TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_EM_SELECTED.format(TagResolver.resolver("roadmap",
						Tag.inserting(roadMap.getDisplayName()))), player);
			}
		}
		if (roadMap == null) {
			TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_EM_SELECT, player);
			return;
		}
		RoadMapHandler.getInstance().getRoadMapEditor(roadMap.getKey()).toggleEditMode(player.getUniqueId());
		TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_EM_ACTIVATED.format(TagResolver.resolver("roadmap",
				Tag.inserting(roadMap.getDisplayName()))), player);
	}

	public void onList(CommandSender sender, Integer page) {

		PathPlayer player = PathPlayerHandler.getInstance().getPlayer(sender);
		NamespacedKey selection = player.getSelectedRoadMap();

		TagResolver resolver = TagResolver.builder()
				.tag("page", Tag.preProcessParsed(page + 1 + ""))
				.tag("prev-page", Tag.preProcessParsed(Integer.max(1, page + 1) + ""))
				.tag("next-page", Tag.preProcessParsed(Integer.min((int) Math.ceil(RoadMapHandler.getInstance().getRoadMaps().size() / 10.), page + 3) + ""))
				.tag("pages", Tag.preProcessParsed((int) Math.ceil(RoadMapHandler.getInstance().getRoadMaps().size() / 10.) + ""))
				.build();

		TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_LIST_HEADER.format(resolver), sender);

		for (RoadMap roadMap : CommandUtils.subList(new ArrayList<>(RoadMapHandler.getInstance().getRoadMaps().values()), page, 10)) {
			TagResolver r = TagResolver.builder()
					.tag("id", Tag.preProcessParsed(roadMap.getKey().getKey() + ""))
					.tag("name", Tag.inserting(roadMap.getDisplayName()))
					.tag("world", Tag.preProcessParsed(roadMap.getWorld().getName()))
					.tag("findable", Tag.inserting(roadMap.isFindableNodes() ?
							Messages.GEN_TRUE.asComponent(sender) : Messages.GEN_FALSE.asComponent(sender)))
					.tag("find-distance", Tag.preProcessParsed(roadMap.getNodeFindDistance() + ""))
					.tag("curve-length", Tag.preProcessParsed(roadMap.getDefaultBezierTangentLength() + ""))
					.tag("path-visualizer", Tag.inserting(roadMap.getVisualizer() == null ? Messages.GEN_NULL.asComponent() : roadMap.getVisualizer().getDisplayName()))
					.build();

			TranslationHandler.getInstance().sendMessage(
					(roadMap.getKey().equals(selection) ? Messages.CMD_RM_LIST_SELECTED : Messages.CMD_RM_LIST_ENTRY)
							.format(resolver, r),
					sender);
		}
		TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_LIST_FOOTER.format(resolver), sender);
	}

	public void onForceFind(CommandSender sender, RoadMap roadMap, Player target, NodeSelection selection) {

		PathPlayer pathPlayer = PathPlayerHandler.getInstance().getPlayer(target.getUniqueId());
		if (pathPlayer == null) {
			return;
		}

		for (Node node : selection) {
			if (node instanceof Discoverable discoverable) {
				DiscoverHandler.getInstance().discover(pathPlayer.getUuid(), discoverable, new Date());
			}
		}
		TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_FORCE_FIND.format(TagResolver.builder()
				.tag("name", Tag.inserting(PathPlugin.getInstance().getAudiences().player(target).getOrDefault(Identity.DISPLAY_NAME, Component.text(target.getName()))))
				.tag("selection", Tag.inserting(Messages.formatNodeSelection(sender, selection)))
				.build()), sender);
	}

	public void onForceForget(CommandSender sender, RoadMap roadMap, Player target, NodeSelection selection) {

		PathPlayer pathPlayer = PathPlayerHandler.getInstance().getPlayer(target.getUniqueId());
		if (pathPlayer == null) {
			return;
		}

		for (Node node : selection) {
			if (node instanceof Discoverable discoverable) {
				DiscoverHandler.getInstance().forget(pathPlayer.getUuid(), discoverable);
			}
		}
		TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_FORCE_FORGET.format(TagResolver.builder()
				.tag("name", Tag.inserting(PathPlugin.getInstance().getAudiences().player(target).getOrDefault(Identity.DISPLAY_NAME, Component.text(target.getName()))))
				.tag("selection", Tag.inserting(Messages.formatNodeSelection(sender, selection))).build()), sender);
	}

	public void onSelect(CommandSender sender, RoadMap roadMap) {
		PathPlayer pathPlayer = PathPlayerHandler.getInstance().getPlayer(sender);

		pathPlayer.setSelectedRoadMap(roadMap.getKey());
		TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_SELECT.format(TagResolver.resolver("name", Tag.inserting(roadMap.getDisplayName()))), sender);
	}

	public void onDeselect(CommandSender sender) {
		PathPlayer pathPlayer = PathPlayerHandler.getInstance().getPlayer(sender);

		pathPlayer.deselectRoadMap();
		TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_DESELECT, sender);
	}

	public void onStyle(CommandSender sender, PathVisualizer<?> visualizer) throws WrapperCommandSyntaxException {
		RoadMap roadMap = CustomArgs.resolveRoadMapWrappedException(sender);
		roadMap.setVisualizer(visualizer);

		TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_SET_VISUALIZER.format(TagResolver.builder()
				.tag("roadmap", Tag.inserting(roadMap.getDisplayName()))
				.tag("visualizer", Tag.inserting(visualizer.getDisplayName()))
				.build()), sender);
	}

	public void onRename(CommandSender sender, String nameNew) throws WrapperCommandSyntaxException {
		RoadMap roadMap = CustomArgs.resolveRoadMapWrappedException(sender);
		Component old = roadMap.getDisplayName();
		roadMap.setNameFormat(nameNew);

		TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_SET_NAME.format(TagResolver.builder()
				.tag("roadmap", Tag.inserting(old))
				.tag("name-format", Tag.inserting(Component.text(nameNew)))
				.tag("display-name", Tag.preProcessParsed(nameNew))
				.build()), sender);
	}

	public void onChangeTangentStrength(CommandSender sender, @Nullable RoadMap roadMap, double strength) throws WrapperCommandSyntaxException {
		roadMap = roadMap != null ? roadMap : CustomArgs.resolveRoadMapWrappedException(sender);
		RoadMapHandler.getInstance().setDefaultCurveLength(roadMap, strength);
		roadMap.setDefaultBezierTangentLength(strength);
		TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_SET_CURVED.format(TagResolver.builder()
				.tag("roadmap", Tag.inserting(roadMap.getDisplayName()))
				.tag("value", Tag.preProcessParsed(String.format("%,.2f", strength)))
				.build()), sender);
	}

	public void onChangeWorld(CommandSender sender, World world, boolean force) throws WrapperCommandSyntaxException {
		RoadMap roadMap = CustomArgs.resolveRoadMapWrappedException(sender);
		RoadMapEditor editor = RoadMapHandler.getInstance().getRoadMapEditor(roadMap.getKey());

		if (!force && editor.isEdited()) {
			TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_CURRENTLY_EDITED, sender);
			return;
		}
		if (editor.isEdited()) {
			editor.cancelEditModes();
		}
		roadMap.setWorld(world);
		TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_SET_WORLD.format(TagResolver.builder()
				.tag("roadmap", Tag.inserting(roadMap.getDisplayName()))
				.tag("world", Tag.inserting(Component.text(world.getName())))
				.build()), sender);
	}

	public void onFindDistance(CommandSender sender, double findDistance) throws WrapperCommandSyntaxException {
		RoadMap roadMap = CustomArgs.resolveRoadMapWrappedException(sender);

		TagResolver resolver = TagResolver.builder()
				.tag("roadmap", Tag.inserting(roadMap.getDisplayName()))
				.tag("value", Tag.preProcessParsed(String.format("%,.2f", findDistance)))
				.build();

		if (findDistance < 0.05) {
			TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_SET_FIND_DIST_TOO_SMALL.format(resolver), sender);
			return;
		}
		roadMap.setNodeFindDistance(findDistance);
		TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_SET_FIND_DIST.format(resolver), sender);
	}


	public void onSetFindable(CommandSender sender, Boolean findable) throws WrapperCommandSyntaxException {
		RoadMap roadMap = CustomArgs.resolveRoadMapWrappedException(sender);
		roadMap.setFindableNodes(findable);
		findable = findable != null && findable;

		TranslationHandler.getInstance().sendMessage(Messages.CMD_RM_SET_FINDABLE.format(TagResolver.builder()
				.tag("roadmap", Tag.inserting(roadMap.getDisplayName()))
				.tag("value", Tag.inserting(Messages.formatBool(findable)))
				.build()), sender);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSelect(RoadmapSelectEvent event) {
		CommandAPI.updateRequirements(event.getPlayer());
	}
}

	/*

	@Subcommand("style")
	@CommandPermission("pathfinder.command.roadmap.style")
	public class RoadmapStyleCommand extends BaseCommand {

		@Subcommand("add")
		@CommandCompletion(PathPlugin.COMPLETE_PATH_VISUALIZER_STYLES)
		@Syntax("<Style>")
		public void onAdd(CommandSender sender, ParticleVisualizer ParticleVisualizer) {
			if (!ParticleVisualizer.isPickable()) {
				PlayerUtils.sendMessage(sender, ChatColor.RED + "Dieser Visualizer ist nicht auswählbar. Konfiguriere ihn mit /path-visualizer.");
				return;
			}
			RoadMap roadMap = CustomArgs.resolveRoadMap(sender);
			Collection<ParticleVisualizer> list = VisualizerHandler.getInstance().getRoadmapVisualizers().getOrDefault(roadMap.getKey(), new ArrayList<>());
			list.add(ParticleVisualizer);
			VisualizerHandler.getInstance().getRoadmapVisualizers().put(roadMap.getKey(), list);
			SqlStorage.getInstance().addStyleToRoadMap(roadMap, ParticleVisualizer);
			PlayerUtils.sendMessage(sender, PathPlugin.PREFIX + "Style hinzugefügt: " + PathPlugin.COLOR_LIGHT + ParticleVisualizer.getNameFormat());
		}


		@Subcommand("remove")
		@CommandCompletion(PathPlugin.COMPLETE_PATH_VISUALIZER_STYLES)
		@Syntax("<Style>")
		public void onRemove(CommandSender sender, ParticleVisualizer visualizer) {
			if (!visualizer.isPickable()) {
				PlayerUtils.sendMessage(sender, ChatColor.RED + "Dieser Visualizer ist nicht auswählbar. Konfiguriere ihn mit /path-visualizer.");
				return;
			}
			RoadMap roadMap = CustomArgs.resolveRoadMap(sender);
			SqlStorage.getInstance().removeStyleFromRoadMap(roadMap, visualizer);
			Collection<ParticleVisualizer> list = VisualizerHandler.getInstance().getRoadmapVisualizers().get(roadMap.getKey());
			if (list != null) {
				list.remove(visualizer);
			}
			PlayerUtils.sendMessage(sender, PathPlugin.PREFIX + "Style entfernt: " + PathPlugin.COLOR_LIGHT + visualizer.getNameFormat());
		}

		@Subcommand("list")
		public void onList(CommandSender sender) {
			Menu menu = new Menu("Alle Styles dieser Roadmap:");
			RoadMap roadMap = CustomArgs.resolveRoadMap(sender);
			for (ParticleVisualizer visualizer : VisualizerHandler.getInstance().getRoadmapVisualizers().getOrDefault(roadMap.getKey(), new ArrayList<>())) {
				menu.addSub(new ComponentMenu(Component.empty()
						.append(visualizer.getDisplayName())
						.append(Component.text(" [X]", NamedTextColor.RED)
								.clickEvent(ClickEvent.runCommand("/roadmap style remove " + visualizer.getNameFormat()))
								.hoverEvent(HoverEvent.showText(Component.text("Klicke zum Entfernen."))))));
			}
			PlayerUtils.sendComponents(sender, menu.toComponents());
		}
	}

	@Subcommand("test")
	@CommandPermission("pathfinder.command.roadmap.test")
	public class RoadMapTestCommand extends BaseCommand {

		@Subcommand("navigate")
		@Syntax("<Findable>")
		@CommandCompletion(PathPlugin.COMPLETE_NODE_SELECTION)
		public void onTestNavigate(Player player, Waypoint findable) {
			RoadMap roadMap = CustomArgs.resolveRoadMap(player);

			PathPlayer pPlayer = PathPlayerHandler.getInstance().getPlayer(player.getUniqueId());
			if (!AStarUtils.startPath(pPlayer, new PlayerNode(player, roadMap), findable, true)) {
				PlayerUtils.sendMessage(player, ChatColor.RED + "Es konnte kein kürzester Pfad ermittelt werden.");
				return;
			}

			player.sendMessage(PathPlugin.PREFIX_COMP
					.append(Component.text("Testpfad gestartet. (", NamedTextColor.GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
					.append(ComponentUtils.getCommandComponent("/cancelpath", ClickEvent.Action.RUN_COMMAND))
					.append(Component.text(")", NamedTextColor.GRAY)));
		}

		@Subcommand("find")
		@Syntax("<Findable>")
		@CommandCompletion(PathPlugin.COMPLETE_FINDABLES_FINDABLE)
		public void onTestFind(Player player, Waypoint findable) {
			RoadMap roadMap = CustomArgs.resolveRoadMap(player);

			PathPlayer pPlayer = PathPlayerHandler.getInstance().getPlayer(player.getUniqueId());
			if (!AStarUtils.startPath(pPlayer, new PlayerNode(player, roadMap), findable, false)) {
				PlayerUtils.sendMessage(player, ChatColor.RED + "Es konnte kein kürzester Pfad ermittelt werden.");
				return;
			}

			player.sendMessage(PathPlugin.PREFIX_COMP
					.append(Component.text("Testpfad gestartet. (", NamedTextColor.GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
					.append(ComponentUtils.getCommandComponent("/cancelpath", ClickEvent.Action.RUN_COMMAND))
					.append(Component.text(")", NamedTextColor.GRAY)));
		}
	}*/
