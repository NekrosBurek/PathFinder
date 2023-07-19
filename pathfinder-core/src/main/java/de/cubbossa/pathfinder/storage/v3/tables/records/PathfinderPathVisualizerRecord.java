/*
 * This file is generated by jOOQ.
 */
package de.cubbossa.pathfinder.storage.v3.tables.records;


import de.cubbossa.pathapi.misc.NamespacedKey;
import de.cubbossa.pathfinder.storage.v3.tables.PathfinderPathVisualizer;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class PathfinderPathVisualizerRecord extends UpdatableRecordImpl<PathfinderPathVisualizerRecord> implements Record6<NamespacedKey, NamespacedKey, String, String, Integer, String> {

  private static final long serialVersionUID = 1L;

  /**
   * Setter for <code>pathfinder_path_visualizer.key</code>.
   */
  public void setKey(NamespacedKey value) {
    set(0, value);
  }

  /**
   * Getter for <code>pathfinder_path_visualizer.key</code>.
   */
  public NamespacedKey getKey() {
    return (NamespacedKey) get(0);
  }

  /**
   * Setter for <code>pathfinder_path_visualizer.type</code>.
   */
  public void setType(NamespacedKey value) {
    set(1, value);
  }

  /**
   * Getter for <code>pathfinder_path_visualizer.type</code>.
   */
  public NamespacedKey getType() {
    return (NamespacedKey) get(1);
  }

  /**
   * Setter for <code>pathfinder_path_visualizer.name_format</code>.
   */
  public void setNameFormat(String value) {
    set(2, value);
  }

  /**
   * Getter for <code>pathfinder_path_visualizer.name_format</code>.
   */
  public String getNameFormat() {
    return (String) get(2);
  }

  /**
   * Setter for <code>pathfinder_path_visualizer.permission</code>.
   */
  public void setPermission(String value) {
    set(3, value);
  }

  /**
   * Getter for <code>pathfinder_path_visualizer.permission</code>.
   */
  public String getPermission() {
    return (String) get(3);
  }

  /**
   * Setter for <code>pathfinder_path_visualizer.interval</code>.
   */
  public void setInterval(Integer value) {
    set(4, value);
  }

  /**
   * Getter for <code>pathfinder_path_visualizer.interval</code>.
   */
  public Integer getInterval() {
    return (Integer) get(4);
  }

  /**
   * Setter for <code>pathfinder_path_visualizer.data</code>.
   */
  public void setData(String value) {
    set(5, value);
  }

  /**
   * Getter for <code>pathfinder_path_visualizer.data</code>.
   */
  public String getData() {
    return (String) get(5);
  }

  // -------------------------------------------------------------------------
  // Primary key information
  // -------------------------------------------------------------------------

  @Override
  public Record1<NamespacedKey> key() {
    return (Record1) super.key();
  }

  // -------------------------------------------------------------------------
  // Record6 type implementation
  // -------------------------------------------------------------------------

  @Override
  public Row6<NamespacedKey, NamespacedKey, String, String, Integer, String> fieldsRow() {
    return (Row6) super.fieldsRow();
  }

  @Override
  public Row6<NamespacedKey, NamespacedKey, String, String, Integer, String> valuesRow() {
    return (Row6) super.valuesRow();
  }

  @Override
  public Field<NamespacedKey> field1() {
    return PathfinderPathVisualizer.PATHFINDER_PATH_VISUALIZER.KEY;
  }

  @Override
  public Field<NamespacedKey> field2() {
    return PathfinderPathVisualizer.PATHFINDER_PATH_VISUALIZER.TYPE;
  }

  @Override
  public Field<String> field3() {
    return PathfinderPathVisualizer.PATHFINDER_PATH_VISUALIZER.NAME_FORMAT;
  }

  @Override
  public Field<String> field4() {
    return PathfinderPathVisualizer.PATHFINDER_PATH_VISUALIZER.PERMISSION;
  }

  @Override
  public Field<Integer> field5() {
    return PathfinderPathVisualizer.PATHFINDER_PATH_VISUALIZER.INTERVAL;
  }

  @Override
  public Field<String> field6() {
    return PathfinderPathVisualizer.PATHFINDER_PATH_VISUALIZER.DATA;
  }

  @Override
  public NamespacedKey component1() {
    return getKey();
  }

  @Override
  public NamespacedKey component2() {
    return getType();
  }

  @Override
  public String component3() {
    return getNameFormat();
  }

  @Override
  public String component4() {
    return getPermission();
  }

  @Override
  public Integer component5() {
    return getInterval();
  }

  @Override
  public String component6() {
    return getData();
  }

  @Override
  public NamespacedKey value1() {
    return getKey();
  }

  @Override
  public NamespacedKey value2() {
    return getType();
  }

  @Override
  public String value3() {
    return getNameFormat();
  }

  @Override
  public String value4() {
    return getPermission();
  }

  @Override
  public Integer value5() {
    return getInterval();
  }

  @Override
  public String value6() {
    return getData();
  }

  @Override
  public PathfinderPathVisualizerRecord value1(NamespacedKey value) {
    setKey(value);
    return this;
  }

  @Override
  public PathfinderPathVisualizerRecord value2(NamespacedKey value) {
    setType(value);
    return this;
  }

  @Override
  public PathfinderPathVisualizerRecord value3(String value) {
    setNameFormat(value);
    return this;
  }

  @Override
  public PathfinderPathVisualizerRecord value4(String value) {
    setPermission(value);
    return this;
  }

  @Override
  public PathfinderPathVisualizerRecord value5(Integer value) {
    setInterval(value);
    return this;
  }

  @Override
  public PathfinderPathVisualizerRecord value6(String value) {
    setData(value);
    return this;
  }

  @Override
  public PathfinderPathVisualizerRecord values(NamespacedKey value1, NamespacedKey value2, String value3, String value4, Integer value5, String value6) {
    value1(value1);
    value2(value2);
    value3(value3);
    value4(value4);
    value5(value5);
    value6(value6);
    return this;
  }

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  /**
   * Create a detached PathfinderPathVisualizerRecord
   */
  public PathfinderPathVisualizerRecord() {
    super(PathfinderPathVisualizer.PATHFINDER_PATH_VISUALIZER);
  }

  /**
   * Create a detached, initialised PathfinderPathVisualizerRecord
   */
  public PathfinderPathVisualizerRecord(NamespacedKey key, NamespacedKey type, String nameFormat, String permission, Integer interval, String data) {
    super(PathfinderPathVisualizer.PATHFINDER_PATH_VISUALIZER);

    setKey(key);
    setType(type);
    setNameFormat(nameFormat);
    setPermission(permission);
    setInterval(interval);
    setData(data);
  }
}
