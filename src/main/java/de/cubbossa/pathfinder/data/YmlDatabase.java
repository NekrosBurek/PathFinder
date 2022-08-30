package de.cubbossa.pathfinder.data;

import de.cubbossa.pathfinder.core.roadmap.RoadMap;
import de.cubbossa.pathfinder.module.visualizing.VisualizerHandler;
import de.cubbossa.pathfinder.module.visualizing.visualizer.ParticleVisualizer;
import de.cubbossa.pathfinder.util.HashedRegistry;
import de.cubbossa.pathfinder.util.NodeSelection;
import de.cubbossa.pathfinder.module.visualizing.visualizer.PathVisualizer;
import de.cubbossa.pathfinder.core.node.*;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jgrapht.alg.util.Triple;
import xyz.xenondevs.particle.ParticleBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class YmlDatabase implements DataStorage {

	private static final String DIR_RM = "roadmaps";
	private static final String DIR_PV = "path_visualizer";

	private File dataDirectory;
	private File roadMapDir;
	private File pathVisualizerDir;

	private final Map<NamespacedKey, YamlConfiguration> roadmapHandles;

	public YmlDatabase(File dataDirectory) {
		if (!dataDirectory.isDirectory()) {
			throw new IllegalArgumentException("Data directory must be a directory!");
		}
		this.dataDirectory = dataDirectory;
		this.roadmapHandles = new HashMap<>();
	}

	@Override
	public void connect() {
		if (!dataDirectory.exists()) {
			dataDirectory.mkdirs();
		}
		this.roadMapDir = new File(dataDirectory, DIR_RM);
		this.roadMapDir.mkdirs();
		this.pathVisualizerDir = new File(dataDirectory, DIR_PV);
		this.pathVisualizerDir.mkdirs();
	}

	@Override
	public void disconnect() {
	}

	@Override
	public RoadMap createRoadMap(NamespacedKey key, String nameFormat, PathVisualizer<?> pathVis, double curveLength) {
		File file = new File(roadMapDir, key.toString().replace(":", "$") + ".yml");
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new DataStorageException("Could not create roadmap file.", e);
		}
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		cfg.set("key", key);
		cfg.set("name_format", nameFormat);
		cfg.set("path_visualizer", pathVis.getKey().toString());
		cfg.set("curve_length", curveLength);

		try {
			cfg.save(file);
		} catch (IOException e) {
			throw new DataStorageException("Could not save roadmap file.", e);
		}
		roadmapHandles.put(key, cfg);
		return new RoadMap(key, nameFormat, pathVis, curveLength);
	}

	@Override
	public Map<NamespacedKey, RoadMap> loadRoadMaps() {
		HashedRegistry<RoadMap> registry = new HashedRegistry<>();
		for (File file : Arrays.stream(roadMapDir.listFiles())
				.filter(file -> file.getName().matches("\\w+_\\w+\\.yml"))
				.collect(Collectors.toList())) {
			try {
				NamespacedKey key = NamespacedKey.fromString(file.getName().substring(0, file.getName().length() - 4).replace("_", ":"));
				YamlConfiguration cfg = roadmapHandles.computeIfAbsent(key, k -> YamlConfiguration.loadConfiguration(file));

				registry.put(new RoadMap(key,
						cfg.getString("name_format"),
						VisualizerHandler.getInstance().getPathVisualizer(NamespacedKey.fromString(cfg.getString("path_visualizer"))),
						cfg.getDouble("curve_length")));


			} catch (Throwable t) {
				//TODO log
			}
		}
		return registry;
	}

	@Override
	public void updateRoadMap(RoadMap roadMap) {

	}

	@Override
	public boolean deleteRoadMap(NamespacedKey key) {
		return false;
	}

	@Override
	public Edge createEdge(Node start, Node end, float weight) {
		return null;
	}

	@Override
	public List<Edge> createEdges(List<Triple<Node, Node, Float>> edges) {
		return null;
	}

	@Override
	public Collection<Edge> loadEdges(RoadMap roadMap) {
		return null;
	}

	@Override
	public void deleteEdgesFrom(Node start) {

	}

	@Override
	public void deleteEdgesTo(Node end) {

	}

	@Override
	public void deleteEdges(Collection<Edge> edges) {

	}

	@Override
	public void deleteEdge(int startId, int endId) {

	}

	@Override
	public <T extends Node> T createNode(RoadMap roadMap, NodeType<T> type, Collection<NodeGroup> groups, Location location, Double tangentLength) {
		return null;
	}

	@Override
	public Map<Integer, Node> loadNodes(RoadMap roadMap) {
		return null;
	}

	@Override
	public void updateNode(Node node) {

	}

	@Override
	public void deleteNodes(Integer... nodeId) {

	}

	@Override
	public void deleteNodes(Collection<Integer> nodeIds) {

	}

	@Override
	public void assignNodesToGroup(NodeGroup group, NodeSelection selection) {

	}

	@Override
	public void removeNodesFromGroup(NodeGroup group, NodeSelection selection) {

	}

	@Override
	public Map<NamespacedKey, List<Integer>> loadNodeGroupNodes() {
		return null;
	}

	@Override
	public NodeGroup createNodeGroup(NamespacedKey key, String nameFormat, @Nullable String permission, boolean navigable, boolean discoverable, double findDistance) {
		return null;
	}

	@Override
	public HashedRegistry<NodeGroup> loadNodeGroups() {
		return null;
	}

	@Override
	public void updateNodeGroup(NodeGroup group) {

	}

	@Override
	public void deleteNodeGroup(NamespacedKey key) {

	}

	@Override
	public Map<NamespacedKey, Collection<String>> loadSearchTerms() {
		return null;
	}

	@Override
	public void addSearchTerms(NodeGroup group, Collection<String> searchTerms) {

	}

	@Override
	public void removeSearchTerms(NodeGroup group, Collection<String> searchTerms) {

	}

	@Override
	public DiscoverInfo createDiscoverInfo(UUID player, Discoverable discoverable, Date foundDate) {
		return null;
	}

	@Override
	public Map<UUID, Map<NamespacedKey, DiscoverInfo>> loadDiscoverInfo() {
		return null;
	}

	@Override
	public void deleteDiscoverInfo(UUID playerId, NamespacedKey discoverKey) {

	}

	@Override
	public PathVisualizer<?> newPathVisualizer(NamespacedKey key, String nameFormat, ParticleBuilder particle, ItemStack displayIcon, double particleDistance, int particleSteps, int schedulerPeriod, double curveLength) {
		return null;
	}

	@Override
	public Map<Integer, PathVisualizer> loadPathVisualizer() {
		return null;
	}

	@Override
	public void updatePathVisualizer(PathVisualizer<?> visualizer) {

	}

	@Override
	public void deletePathVisualizer(PathVisualizer<?> visualizer) {

	}

	@Override
	public Map<Integer, Map<Integer, Integer>> loadPlayerVisualizers() {
		return null;
	}

	@Override
	public void createPlayerVisualizer(int playerId, RoadMap roadMap, ParticleVisualizer visualizer) {

	}

	@Override
	public void updatePlayerVisualizer(int playerId, RoadMap roadMap, ParticleVisualizer visualizer) {

	}

	@Override
	public void loadVisualizerStyles(Collection<ParticleVisualizer> visualizers) {

	}

	@Override
	public void newVisualizerStyle(ParticleVisualizer visualizer, @Nullable String permission, @Nullable Material iconType, @Nullable String miniDisplayName) {

	}

	@Override
	public void updateVisualizerStyle(ParticleVisualizer visualizer) {

	}

	@Override
	public void deleteStyleVisualizer(int visualizerId) {

	}

	@Override
	public Map<Integer, Collection<ParticleVisualizer>> loadStyleRoadmapMap(Collection<ParticleVisualizer> visualizers) {
		return null;
	}

	@Override
	public void addStyleToRoadMap(RoadMap roadMap, ParticleVisualizer ParticleVisualizer) {

	}

	@Override
	public void removeStyleFromRoadMap(RoadMap roadMap, ParticleVisualizer ParticleVisualizer) {

	}

	@Override
	public NodeBatchCreator newNodeBatch() {
		return null;
	}
}
