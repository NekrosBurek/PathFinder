package de.cubbossa.pathfinder.util;

import com.google.common.collect.Lists;
import de.cubbossa.pathfinder.core.node.Node;
import de.cubbossa.pathfinder.core.roadmap.RoadMapHandler;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SelectionUtils {

	@Getter
	public static class PlayerContext extends SelectionParser.Context {

		private final Player player;

		public PlayerContext(String value, Player player) {
			super(value);
			this.player = player;
		}
	}

	public static final SelectionParser.Filter<Node, PlayerContext> SELECT_KEY_TANGENT_LENGTH = new SelectionParser.Filter<>("tangent_length", Pattern.compile("(\\.\\.)?[0-9]*(\\.[0-9]+)?(\\.\\.)?"), (nodes, context) -> {
		boolean smaller = context.value().startsWith("..");
		boolean larger = context.value().endsWith("..");
		if (smaller && larger) {
			return nodes;
		}
		String arg = context.value();
		if (smaller) {
			arg = arg.substring(2);
		}
		if (larger) {
			arg = arg.substring(0, arg.length() - 2);
		}
		float req = Float.parseFloat(arg);
		return nodes.stream().filter(node -> {
			double dist = node.getLocation().distance(context.getPlayer().getLocation());
			return smaller && dist <= req || larger && dist >= req || dist == req;
		}).collect(Collectors.toList());
	}, "..1", "1.5", "2..");

	public static final SelectionParser.Filter<Node, PlayerContext> SELECT_KEY_DISTANCE = new SelectionParser.Filter<>("distance", Pattern.compile("(\\.\\.)?[0-9]*(\\.[0-9]+)?(\\.\\.)?"), (nodes, context) -> {
		boolean smaller = context.value().startsWith("..");
		boolean larger = context.value().endsWith("..");
		if (smaller && larger) {
			return nodes;
		}
		String arg = context.value();
		if (smaller) {
			arg = arg.substring(2);
		}
		if (larger) {
			arg = arg.substring(0, arg.length() - 2);
		}
		float req = Float.parseFloat(arg);
		return nodes.stream().filter(node -> {
			double dist = node.getLocation().distance(context.getPlayer().getLocation());
			return smaller && dist <= req || larger && dist >= req || dist == req;
		}).collect(Collectors.toList());
	}, "..1", "1.5", "2..");

	public static final SelectionParser.Filter<Node, PlayerContext> SELECT_KEY_LIMIT = new SelectionParser.Filter<>("limit", Pattern.compile("[0-9]+"), (nodes, context) -> CommandUtils.subList(new ArrayList<>(nodes), 0, Integer.parseInt(context.value())), IntStream.range(1, 10).mapToObj(i -> "" + i).toArray(String[]::new));

	public static final SelectionParser.Filter<Node, PlayerContext> SELECT_KEY_SORT = new SelectionParser.Filter<>("sort", Pattern.compile("(nearest|furthest|random|arbitrary)"), (nodes, context) -> {
		Location pLoc = context.getPlayer().getLocation();
		return switch (context.value()) {
			case "nearest" -> nodes.stream().sorted(Comparator.comparingDouble(o -> o.getLocation().distance(pLoc))).collect(Collectors.toList());
			case "furthest" -> nodes.stream()
					.sorted((o1, o2) -> Double.compare(o2.getLocation().distance(pLoc), o1.getLocation().distance(pLoc)))
					.collect(Collectors.toList());
			case "random" -> nodes.stream().collect(Collectors.collectingAndThen(Collectors.toList(), n -> {
				Collections.shuffle(n);
				return n;
			}));
			case "arbitrary" -> nodes.stream().sorted(Comparator.comparingInt(Node::getNodeId)).collect(Collectors.toList());
			default -> nodes;
		};
	}, "nearest", "furthest", "random", "arbitrary");

	public static final String SELECT_KEY_GROUP = "group";

	public static final String SELECT_KEY_EDGE = "has_edge";

	public static final ArrayList<SelectionParser.Filter<Node, PlayerContext>> SELECTORS = Lists.newArrayList(
			SELECT_KEY_LIMIT, SELECT_KEY_DISTANCE, SELECT_KEY_TANGENT_LENGTH,
			SELECT_KEY_SORT
	);

	public static NodeSelection getNodeSelection(Player player, String selectString) {
		return new SelectionParser<>(SELECTORS, string -> new PlayerContext(string, player), "n", "node")
				.parseSelection(RoadMapHandler.getInstance().getRoadMaps().values().stream()
						.flatMap(roadMap -> roadMap.getNodes().stream()).collect(Collectors.toSet()), selectString, NodeSelection::new);
	}

	public static List<String> completeNodeSelection(String selectString) {
		if (!selectString.startsWith("@n[")) {
			return Lists.newArrayList("@n[");
		}
		String in = selectString;
		selectString = selectString.substring(3);

		String[] args = selectString.split(",");
		selectString = args[args.length - 1];

		String sel = selectString;
		String sub = in.substring(0, Integer.max(in.lastIndexOf(','), in.lastIndexOf('[')) + 1);

		return SELECTORS.stream()
				.map(s -> sel.endsWith("=") ? Arrays.stream(s.completions()).map(c -> s.key() + "=" + c).collect(Collectors.toList()) : Lists.newArrayList(s.key() + "="))
				.flatMap(Collection::stream)
				.map(s -> sub + s)
				.collect(Collectors.toList());
	}
}
