/*
 * This file is generated by jOOQ.
 */
package de.cubbossa.pathfinder.storage.v3.tables.records;


import de.cubbossa.pathapi.misc.NamespacedKey;
import de.cubbossa.pathfinder.storage.v3.tables.PathfinderNodegroupsNodes;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class PathfinderNodegroupsNodesRecord extends UpdatableRecordImpl<PathfinderNodegroupsNodesRecord> implements Record2<NamespacedKey, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>pathfinder_nodegroups_nodes.group_key</code>.
     */
    public void setGroupKey(NamespacedKey value) {
        set(0, value);
    }

    /**
     * Getter for <code>pathfinder_nodegroups_nodes.group_key</code>.
     */
    public NamespacedKey getGroupKey() {
        return (NamespacedKey) get(0);
    }

    /**
     * Setter for <code>pathfinder_nodegroups_nodes.node_id</code>.
     */
    public void setNodeId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>pathfinder_nodegroups_nodes.node_id</code>.
     */
    public Integer getNodeId() {
        return (Integer) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<NamespacedKey, Integer> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<NamespacedKey, Integer> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<NamespacedKey, Integer> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<NamespacedKey> field1() {
        return PathfinderNodegroupsNodes.PATHFINDER_NODEGROUPS_NODES.GROUP_KEY;
    }

    @Override
    public Field<Integer> field2() {
        return PathfinderNodegroupsNodes.PATHFINDER_NODEGROUPS_NODES.NODE_ID;
    }

    @Override
    public NamespacedKey component1() {
        return getGroupKey();
    }

    @Override
    public Integer component2() {
        return getNodeId();
    }

    @Override
    public NamespacedKey value1() {
        return getGroupKey();
    }

    @Override
    public Integer value2() {
        return getNodeId();
    }

    @Override
    public PathfinderNodegroupsNodesRecord value1(NamespacedKey value) {
        setGroupKey(value);
        return this;
    }

    @Override
    public PathfinderNodegroupsNodesRecord value2(Integer value) {
        setNodeId(value);
        return this;
    }

    @Override
    public PathfinderNodegroupsNodesRecord values(NamespacedKey value1, Integer value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PathfinderNodegroupsNodesRecord
     */
    public PathfinderNodegroupsNodesRecord() {
        super(PathfinderNodegroupsNodes.PATHFINDER_NODEGROUPS_NODES);
    }

    /**
     * Create a detached, initialised PathfinderNodegroupsNodesRecord
     */
    public PathfinderNodegroupsNodesRecord(NamespacedKey groupKey, Integer nodeId) {
        super(PathfinderNodegroupsNodes.PATHFINDER_NODEGROUPS_NODES);

        setGroupKey(groupKey);
        setNodeId(nodeId);
    }
}
