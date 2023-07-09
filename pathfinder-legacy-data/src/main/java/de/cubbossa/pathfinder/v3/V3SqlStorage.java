package de.cubbossa.pathfinder.v3;

import de.cubbossa.pathapi.misc.NamespacedKey;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.util.*;

import static de.cubbossa.pathfinder.v3.tables.PathfinderDiscoverings.PATHFINDER_DISCOVERINGS;
import static de.cubbossa.pathfinder.v3.tables.PathfinderEdges.PATHFINDER_EDGES;
import static de.cubbossa.pathfinder.v3.tables.PathfinderNodegroups.PATHFINDER_NODEGROUPS;
import static de.cubbossa.pathfinder.v3.tables.PathfinderNodes.PATHFINDER_NODES;
import static de.cubbossa.pathfinder.v3.tables.PathfinderPathVisualizer.PATHFINDER_PATH_VISUALIZER;
import static de.cubbossa.pathfinder.v3.tables.PathfinderRoadmaps.PATHFINDER_ROADMAPS;
import static de.cubbossa.pathfinder.v3.tables.PathfinderSearchTerms.PATHFINDER_SEARCH_TERMS;

@Deprecated
public abstract class V3SqlStorage implements V3Storage {

  abstract ConnectionProvider getConnectionProvider();

  private DSLContext context;
  private final SQLDialect dialect;

  public V3SqlStorage(SQLDialect dialect) {
    this.dialect = dialect;
  }

  @Override
  public void connect() {

    System.setProperty("org.jooq.no-logo", "true");
    System.setProperty("org.jooq.no-tips", "true");

    context = DSL.using(getConnectionProvider(), dialect, new Settings()
        .withRenderQuotedNames(RenderQuotedNames.ALWAYS)
        .withRenderSchema(dialect != SQLDialect.SQLITE));
  }

  @Override
  public Collection<V3RoadMap> loadRoadmaps() {
    return context
        .selectFrom(PATHFINDER_ROADMAPS)
        .fetch(record -> new V3RoadMap(
            record.getKey(),
            record.getNameFormat(),
            NamespacedKey.fromString(record.getPathVisualizer()),
            record.getPathCurveLength()
        ));
  }

  @Override
  public Collection<V3Edge> loadEdges() {
    return context
        .selectFrom(PATHFINDER_EDGES)
        .fetch(record -> new V3Edge(
            record.getStartId(), record.getEndId(), record.getWeightModifier().floatValue()
        ));
  }

  @Override
  public Collection<V3Node> loadNodes() {
    return context
        .selectFrom(PATHFINDER_NODES)
        .fetch(record -> new V3Node(
            record.getId(),
            record.getType(),
            record.getRoadmapKey(),
            record.getX(),
            record.getY(),
            record.getZ(),
            UUID.fromString(record.getWorld()),
            record.getPathCurveLength()
        ));
  }

  @Override
  public Collection<> loadNodeGroupNodes() {
    Map<Integer, HashSet<NamespacedKey>> result = new LinkedHashMap<>();
    context
        .selectFrom(PATHFINDER_NODEGROUP_NODES)
        .fetch()
        .forEach(record -> {
          result.computeIfAbsent(record.getNodeId(), id -> new HashSet<>()).add(record.getGroupKey());
        });
    return result;
  }

  @Override
  public Collection<V3NodeGroup> loadNodeGroups() {
    return context
        .selectFrom(PATHFINDER_NODEGROUPS)
        .fetch(record -> new V3NodeGroup(
            record.getKey(),
            record.getNameFormat(),
            record.getPermission(),
            record.getNavigable(),
            record.getDiscoverable(),
            record.getFindDistance()
        ));
  }

  @Override
  public Collection<V3SearchTerm> loadSearchTerms() {
    return context
        .selectFrom(PATHFINDER_SEARCH_TERMS)
        .fetch(record -> new V3SearchTerm(record.getGroupKey(), record.getSearchTerm()));
  }

  @Override
  public Collection<V3Discovering> loadDiscoverings() {
    return context
        .selectFrom(PATHFINDER_DISCOVERINGS)
        .fetch(record -> new V3Discovering(
            UUID.fromString(record.getPlayerId()), record.getDiscoverKey(), record.getDate())
        );
  }

  @Override
  public Collection<V3Visualizer> loadVisualizers() {
    return context
        .selectFrom(PATHFINDER_PATH_VISUALIZER)
        .fetch(record -> new V3Visualizer(
            record.getKey(),
            record.getType(),
            record.getNameFormat(),
            record.getPermission(),
            record.getInterval(),
            record.getData()
        ));
  }
}
