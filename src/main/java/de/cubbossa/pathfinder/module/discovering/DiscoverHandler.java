package de.cubbossa.pathfinder.module.discovering;

import de.cubbossa.pathfinder.PathPlugin;
import de.cubbossa.pathfinder.core.node.Discoverable;
import de.cubbossa.pathfinder.core.roadmap.RoadMap;
import de.cubbossa.pathfinder.data.DiscoverInfo;
import de.cubbossa.serializedeffects.EffectHandler;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class DiscoverHandler {

	@Getter
	private static DiscoverHandler instance;

	private final Map<UUID, Map<NamespacedKey, DiscoverInfo>> discovered;

	public DiscoverHandler() {
		instance = this;

		discovered = new HashMap<>();
		Bukkit.getPluginManager().registerEvents(new MoveListener(), PathPlugin.getInstance());
	}

	public void playDiscovery(UUID playerId, Discoverable discoverable) {
		Player player = Bukkit.getPlayer(playerId);
		if (player == null) {
			throw new IllegalStateException("Player is null");
		}
		EffectHandler.getInstance().getEffect(PathPlugin.getInstance().getEffectsFile(), "discover")
				.play(player, player.getLocation(), TagResolver.builder()
						.tag("name", Tag.inserting(discoverable.getDisplayName()))
						.tag("percent", Tag.inserting(Component.text("10%fm")))
						.build());
	}

	public void discover(UUID playerId, Discoverable discoverable, Date date) {
		if(hasDiscovered(playerId, discoverable)) {
			return;
		}
		discovered.computeIfAbsent(playerId, uuid -> new HashMap<>()).put(discoverable.getUniqueKey(), new DiscoverInfo(playerId, discoverable, date));
		playDiscovery(playerId, discoverable);
	}

	public void forget(UUID playerId, Discoverable discoverable) {

	}

	public boolean hasDiscovered(UUID playerId, Discoverable discoverable) {
		return discovered.computeIfAbsent(playerId, uuid -> new HashMap<>()).containsKey(discoverable.getUniqueKey());
	}

	public Collection<Discoverable> getDiscovered(UUID playerId, RoadMap roadMap) {
		return discovered.computeIfAbsent(playerId, uuid -> new HashMap<>()).values().stream()
				.map(DiscoverInfo::discoverable)
				.collect(Collectors.toSet());
	}

	public int getDiscoveredCount(UUID playerId, RoadMap roadMap) {
		return getDiscovered(playerId, roadMap).size();
	}

	public float getDiscoveredPercent(UUID uuid, RoadMap roadMap) {
		int count = 0, sum = 0;
		for (Discoverable discoverable : getDiscovered(uuid, roadMap)) {
			count += discoverable.getDiscoveringWeight();
		}
		/* TODO for (Discoverable discoverable : roadMap.getDiscoverables()) {
			sum += discoverable.getDiscoveringWeight();
		}*/
		return count / (float) sum;
	}

	public float getDiscoveryDistance(UUID playerId, RoadMap roadMap) {
		return 3f;
	}
}